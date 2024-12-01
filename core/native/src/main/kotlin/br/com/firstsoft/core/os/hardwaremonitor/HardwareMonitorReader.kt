package br.com.firstsoft.core.os.hardwaremonitor

import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.os.util.getByteBuffer
import br.com.firstsoft.core.os.util.readString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

private const val HARDWARE_SIZE = 260
private const val SENSOR_SIZE = 392
private const val NAME_SIZE = 128
private const val IDENTIFIER_SIZE = 128
private const val HEADER_SIZE = 8

object HardwareMonitorReader {

    val currentData = flow {
        var socket = Socket()

        while (true) {

            // try open a connection with HardwareMonitor
            if (!socket.isConnected) {
                try {
                    socket = Socket()
                    socket.connect(InetSocketAddress("127.0.0.1", 31337))
                } catch (ex: Exception) {
                    if (ex !is SocketException) {
                        ex.printStackTrace()
                    }
                } finally {
                    delay(500)
                    continue
                }
            }

            val inputStream = socket.inputStream
            while (socket.isConnected) {
                try {
                    // read first 8 bytes to get the amount of hardware and sensors
                    val (hardware, sensor) = readHardwareAndSensorCount(inputStream)

                    // if both are 0, bail
                    if (hardware + sensor == 0) continue

                    // we know the length in bytes of hardware and sensor, so we know the length of the packet
                    val buffer = getByteBuffer(inputStream, hardware * HARDWARE_SIZE + sensor * SENSOR_SIZE)
                    val hardwares = readHardware(buffer, hardware)
                    val sensors = readSensor(buffer, sensor)
                    emit(HardwareMonitorData(0L, hardwares, sensors))
                } catch (e: SocketException) {
                    socket.close()
                    socket = Socket()
                    e.printStackTrace()
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun readHardwareAndSensorCount(input: InputStream): Pair<Int, Int> {
        val buffer = getByteBuffer(input, HEADER_SIZE)
        return buffer.int to buffer.int
    }

    private fun readHardware(buffer: ByteBuffer, count: Int): List<HardwareMonitorData.Hardware> {
        return buildList {
            for (i in 0 until count) {
                val hardware = HardwareMonitorData.Hardware(
                    Name = buffer.readString(NAME_SIZE),
                    Identifier = buffer.readString(IDENTIFIER_SIZE),
                    HardwareType = HardwareMonitorData.HardwareType.fromValue(buffer.int),
                )
                add(hardware)
            }
        }
    }

    private fun readSensor(buffer: ByteBuffer, count: Int): List<HardwareMonitorData.Sensor> {
        return buildList {
            for (i in 0 until count) {
                val sensor = HardwareMonitorData.Sensor(
                    Name = buffer.readString(NAME_SIZE),
                    Identifier = buffer.readString(IDENTIFIER_SIZE),
                    HardwareIdentifier = buffer.readString(IDENTIFIER_SIZE),
                    SensorType = HardwareMonitorData.SensorType.fromValue(buffer.int),
                    Value = buffer.float,
                )
                add(sensor)
            }
        }
    }
}