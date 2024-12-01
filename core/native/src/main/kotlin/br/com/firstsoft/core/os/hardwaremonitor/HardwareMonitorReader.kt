package br.com.firstsoft.core.os.hardwaremonitor

import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.os.util.getByteBuffer
import br.com.firstsoft.core.os.util.readString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer

private const val HARDWARE_SIZE = 260
private const val SENSOR_SIZE = 392
private const val NAME_SIZE = 128
private const val IDENTIFIER_SIZE = 128
private const val HEADER_SIZE = 8

object HardwareMonitorReader {

    val currentData = flow {
        val socket = Socket()
        while (true) {
            if (!socket.isConnected) {
                try {
                    socket.connect(InetSocketAddress("127.0.0.1", 31337), 200)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    delay(1000)
                }
            }

            val inputStream = socket.inputStream
            while (socket.isConnected) {
                try {
                    val (hardware, sensor) = readHardwareAndSensorCount(inputStream)
                    if (hardware + sensor == 0) continue

                    val buffer = getByteBuffer(inputStream, hardware * HARDWARE_SIZE + sensor * SENSOR_SIZE)
                    val hardwares = readHardware(buffer, hardware)
                    val sensors = readSensor(buffer, sensor)
                    emit(HardwareMonitorData(0L, hardwares, sensors))
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
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