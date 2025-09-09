package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.PREFERENCE_PERMISSION_CONSENT
import app.cleanmeter.core.os.PreferencesRepository
import app.cleanmeter.core.os.util.getByteBuffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
                        Command.Data -> packetChannel.trySend(Packet.Data(inputStream.readNBytes(size)))
                        Command.PresentMonApps -> packetChannel.trySend(Packet.PresentMonApps(inputStream.readNBytes(size)))
                        Command.RefreshPresentMonApps -> Unit
                        Command.SelectPresentMonApp -> Unit
                        Command.SelectPollingRate -> Unit
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
    private var pipeInputStream: FileInputStream? = null
    private var pipeOutputStream: FileOutputStream? = null
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
            if (!isConnected()) {
                try {
                    println("Trying to connect to pipe: $pipeName")
                    close()

                    // Open pipe as stream - this should work correctly
                    pipeInputStream = FileInputStream(pipeName)
                    // Don't open output stream until we need to send
                    println("Connected to named pipe for reading")
                } catch (ex: Exception) {
                    println("Couldn't connect to pipe: ${ex.message}")
                    ex.printStackTrace()
                    close()
                    delay(pollingRate)
                    continue
                }
            }

            pipeInputStream?.let { inputStream ->
                try {
                    while (isConnected()) {

                        // Read command (2 bytes)
                        val commandBytes = readExactly(inputStream, COMMAND_SIZE)
                        val command = Command.fromValue(ByteBuffer.wrap(commandBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).short)

                        // Read size (4 bytes)
                        val sizeBytes = readExactly(inputStream, LENGTH_SIZE)
                        val size = ByteBuffer.wrap(sizeBytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).int

                        if (size < 0) { // Sanity check
                            break
                        }

                        // Read payload
                        val payload = readExactly(inputStream, size)
                        when (command) {
                            Command.Data -> packetChannel.trySend(Packet.Data(payload))
                            Command.PresentMonApps -> packetChannel.trySend(Packet.PresentMonApps(payload))
                            Command.RefreshPresentMonApps -> Unit
                            Command.SelectPresentMonApp -> Unit
                            Command.SelectPollingRate -> Unit
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

    private fun isConnected(): Boolean {
        return pipeInputStream != null
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
        if (pipeOutputStream == null && pipeInputStream != null) {
            try {
                pipeOutputStream = FileOutputStream(pipeName, true) // append mode
            } catch (e: Exception) {
                println("Error opening output stream: ${e.message}")
                return
            }
        }

        pipeOutputStream?.let { stream ->
            try {
                val data = packet.toByteArray()
                stream.write(data)
                stream.flush()
            } catch (e: Exception) {
                println("Error sending packet: ${e.message}")
                pipeOutputStream?.close()
                pipeOutputStream = null
            }
        }
    }

    fun close() {
        try {
            pipeInputStream?.close()
            pipeOutputStream?.close()
        } catch (e: Exception) {
            println("Error closing pipe: ${e.message}")
        } finally {
            pipeInputStream = null
            pipeOutputStream = null
        }
    }
}