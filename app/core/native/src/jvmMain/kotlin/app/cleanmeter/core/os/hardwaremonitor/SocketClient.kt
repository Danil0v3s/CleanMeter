package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.PREFERENCE_PERMISSION_CONSENT
import app.cleanmeter.core.os.Platform
import app.cleanmeter.core.os.PlatformService.getForegroundProcessName
import app.cleanmeter.core.os.PreferencesRepository
import app.cleanmeter.core.os.getCurrentPlatform
import app.cleanmeter.core.os.hardwaremonitor.Packet.*
import app.cleanmeter.core.os.util.getByteBuffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.FileSystems

private const val COMMAND_SIZE = 2
private const val LENGTH_SIZE = 4

sealed class Packet {
    open fun toByteArray(): ByteArray = ByteArray(0)

    data class Data(val data: ByteArray) : Packet()
    data class PresentMonApps(val data: ByteArray) : Packet()
    data class SelectPresentMonApp(val name: String) : Packet() {
        override fun toByteArray(): ByteArray {
            val nameBytes = name.toByteArray()
            val buffer = ByteBuffer.allocate(2 + 2 + nameBytes.count()).order(ByteOrder.LITTLE_ENDIAN).apply {
                putShort(Command.SelectPresentMonApp.value)
                putShort(nameBytes.size.toShort())
                put(nameBytes)
            }.array()
            return buffer
        }
    }
    data class SetForegroundApplication(val name: String) : Packet() {
        override fun toByteArray(): ByteArray {
            val nameBytes = name.toByteArray()
            val buffer = ByteBuffer.allocate(2 + 2 + nameBytes.count()).order(ByteOrder.LITTLE_ENDIAN).apply {
                putShort(Command.SetForegroundApplication.value)
                putShort(nameBytes.size.toShort())
                put(nameBytes)
            }.array()
            return buffer
        }
    }

    data class SelectPollingRate(val interval: Short) : Packet() {
        override fun toByteArray(): ByteArray {
            val buffer = ByteBuffer.allocate(2 + 2).order(ByteOrder.LITTLE_ENDIAN).apply {
                putShort(Command.SelectPollingRate.value)
                putShort(interval)
            }.array()
            return buffer
        }
    }
}

object SocketClient {

    private var socket = Socket()
    private var pollingRate = 500L

    private val packetChannel = Channel<Packet>(Channel.CONFLATED)
    val packetFlow: Flow<Packet> = packetChannel.receiveAsFlow()

    init {
        if (PreferencesRepository.getPreferenceBoolean(PREFERENCE_PERMISSION_CONSENT, false)) {
            connect()
        }
    }

    private fun connect() = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            // try open a connection with HardwareMonitor
            if (!socket.isConnected) {
                try {
                    println("Trying to connect")
                    socket = Socket()
                    socket.connect(InetSocketAddress(InetAddress.getLoopbackAddress(), 31337))
                    println("Connected ${socket.isConnected}")
                } catch (ex: Exception) {
                    println("Couldn't connect ${ex.message}")
                    ex.printStackTrace()
                } finally {
                    delay(pollingRate)
                    continue
                }
            }

            val inputStream = socket.inputStream
            while (socket.isConnected) {
                try {
                    val command = getCommand(inputStream)
                    val size = getSize(inputStream)
                    when (command) {
                        Command.Data -> packetChannel.trySend(Data(inputStream.readNBytes(size)))
                        Command.PresentMonApps -> packetChannel.trySend(PresentMonApps(inputStream.readNBytes(size)))
                        Command.RefreshPresentMonApps -> Unit
                        Command.SelectPresentMonApp -> Unit
                        Command.SelectPollingRate -> Unit
                        Command.SetForegroundApplication -> Unit
                    }
                } catch (e: SocketException) {
                    println("Error while listening for packets")
                    socket.close()
                    socket = Socket()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getCommand(inputStream: InputStream): Command {
        val buffer = getByteBuffer(inputStream, COMMAND_SIZE)
        return Command.fromValue(buffer.short)
    }

    private fun getSize(inputStream: InputStream): Int {
        val buffer = getByteBuffer(inputStream, LENGTH_SIZE)
        return buffer.int
    }

    fun setPollingRate(pollingRate: Long) {
        println("Setting PollingRate to $pollingRate")
        this.pollingRate = pollingRate
    }

    fun sendPacket(packet: Packet) {
        if (socket.isConnected) {
            socket.outputStream.apply {
                write(packet.toByteArray())
                flush()
            }
        }
    }
}

object PipeClient {

    private val pipeName = "\\\\.\\pipe\\HardwareMonitor_31337"
    private var pipeFile: RandomAccessFile? = null
    private var pollingRate = 500L

    private val packetChannel = Channel<Packet>(Channel.CONFLATED)
    val packetFlow: Flow<Packet> = packetChannel.receiveAsFlow()

    init {
        if (PreferencesRepository.getPreferenceBoolean(PREFERENCE_PERMISSION_CONSENT, false)) {
            connect()
        }
    }

    private fun connect() {
        connectToNamedPipe()
        observeFocusedProcess()
    }

    private fun connectToNamedPipe() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (!isConnected()) {
                    try {
                        println("Trying to connect to pipe: $pipeName")
                        close()

                        // Open pipe as stream - this should work correctly
                        pipeFile = RandomAccessFile(pipeName, "rw")
                        // Don't open output stream until we need to send
                        println("Connected to named pipe for reading")
                    } catch (ex: Exception) {
    //                    println("Couldn't connect to pipe: ${ex.message}")
    //                    ex.printStackTrace()
                        close()
                        delay(pollingRate)
                        continue
                    }
                }

                pipeFile?.let { raf ->
                    try {
                        while (isConnected()) {

                            val commandBytes = ByteArray(COMMAND_SIZE)
                            raf.readFully(commandBytes)
                            val command = Command.fromValue(
                                ByteBuffer.wrap(commandBytes)
                                    .order(ByteOrder.LITTLE_ENDIAN)
                                    .short
                            )

                            val sizeBytes = ByteArray(LENGTH_SIZE)
                            raf.readFully(sizeBytes)
                            val size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).int

                            val payload = ByteArray(size)
                            raf.readFully(payload)

                            when (command) {
                                Command.Data -> packetChannel.trySend(Data(payload))
                                Command.PresentMonApps -> packetChannel.trySend(PresentMonApps(payload))
                                Command.RefreshPresentMonApps -> Unit
                                Command.SelectPresentMonApp -> Unit
                                Command.SelectPollingRate -> Unit
                                Command.SetForegroundApplication -> Unit
                            }
                        }
                    } catch (e: Exception) {
                        println("Error while listening for packets: ${e.message}")
                        e.printStackTrace()
                        close()
                    }
                }
            }
        }
    }

    private fun observeFocusedProcess() {
        CoroutineScope(Dispatchers.IO).launch {
            var currentFocusedProcess: String? = null
            while (true) {
                when (getCurrentPlatform()) {
                    Platform.WINDOWS -> {
                        val foregroundProcessName = getForegroundProcessName()?.split(
                            FileSystems.getDefault().separator
                        )?.last() ?: continue

                        if (foregroundProcessName != currentFocusedProcess) {
                            currentFocusedProcess = foregroundProcessName
                            println("Foreground process: $foregroundProcessName")
                            sendPacket(SetForegroundApplication(foregroundProcessName))
                        }
                    }

                    Platform.MACOS -> println("TODO macos")
                    Platform.LINUX -> println("TODO linux")
                }
                delay(2000L)
            }
        }
    }

    private fun isConnected(): Boolean {
        return pipeFile != null
    }

    // Helper function to read exactly n bytes from InputStream
    private fun readExactly(inputStream: InputStream, count: Int): ByteArray {
        val buffer = ByteArray(count)
        var totalRead = 0

        while (totalRead < count) {
            val bytesRead = inputStream.read(buffer, totalRead, count - totalRead)
            if (bytesRead == -1) {
                throw IOException("Pipe closed while reading")
            }
            totalRead += bytesRead
        }

        return buffer
    }

    fun setPollingRate(pollingRate: Long) {
        println("Setting PollingRate to $pollingRate")
        this.pollingRate = pollingRate
    }

    fun sendPacket(packet: Packet) {
        // Open output stream only when needed
        pipeFile?.let { raf ->
            try {
                val data = packet.toByteArray()
                raf.write(data)
                raf.fd.sync()
            } catch (e: Exception) {
                println("Error sending packet: ${e.message}")
                close()
            }
        }
    }

    fun close() {
        try {
            pipeFile?.close()
        } catch (e: Exception) {
            println("Error closing pipe: ${e.message}")
        } finally {
            pipeFile = null
        }
    }
}