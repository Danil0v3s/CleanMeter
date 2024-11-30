package br.com.firstsoft.core.os.hardwaremonitor

import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.os.util.getByteBuffer
import br.com.firstsoft.core.os.util.readString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.InputStream
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.coroutines.cancellation.CancellationException

private const val HARDWARE_SIZE = 260
private const val SENSOR_SIZE = 392
private const val NAME_SIZE = 128
private const val IDENTIFIER_SIZE = 128
private const val HEADER_SIZE = 8

object HardwareMonitorReader {

    val currentData = flow {
        delay(5000)
        val socket = Socket("127.0.0.1", 31337)

        while (socket.isConnected) {
            try {
                if (socket.getInputStream().available() == 0) continue
                val (hardware, sensor) = readHardwareAndSensorCount(socket.getInputStream())
                val hardwareBuffer = getByteBuffer(socket.getInputStream(), hardware * HARDWARE_SIZE)
                val sensorBuffer = getByteBuffer(socket.getInputStream(), sensor * SENSOR_SIZE)
                val hardwares = readHardware(hardwareBuffer, hardware)
                val sensors = readSensor(sensorBuffer, sensor)
                emit(HardwareMonitorData(0L, hardwares, sensors))
            } catch (e: CancellationException) {
                break
            }
        }
    }
        .flowOn(Dispatchers.IO)

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