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
import java.io.InputStream
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
