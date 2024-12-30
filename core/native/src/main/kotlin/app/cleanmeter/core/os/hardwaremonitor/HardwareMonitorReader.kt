package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.os.util.getByteBuffer
import app.cleanmeter.core.os.util.readString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.nio.ByteBuffer

private const val HARDWARE_SIZE = 260
private const val SENSOR_SIZE = 392
private const val NAME_SIZE = 128
private const val IDENTIFIER_SIZE = 128
private const val HEADER_SIZE = 8
private const val LENGTH_SIZE = 2

enum class Command(val value: Short) {
    Data(0),
    RefreshPresentMonApps(1),
    SelectPresentMonApp(2),
    PresentMonApps(3),
    SelectPollingRate(4),
    ;

    companion object {
        fun fromValue(value: Short) = entries.find { it.value == value } ?: Data
    }
}

object HardwareMonitorReader {

    private var _currentData: HardwareMonitorData = HardwareMonitorData(0L, emptyList(), emptyList(), emptyList())
    val currentData: Flow<HardwareMonitorData> = SocketClient
        .packetFlow
        .mapNotNull { packet ->
            when (packet) {
                is Packet.Data -> {
                    // read first 8 bytes to get the amount of hardware and sensors
                    val (hardware, sensor) = readHardwareAndSensorCount(packet.data)
                    if (hardware + sensor <= 0) return@mapNotNull null

                    val buffer = getByteBuffer(packet.data, hardware * HARDWARE_SIZE + sensor * SENSOR_SIZE, HEADER_SIZE)
                    val hardwares = readHardware(buffer, hardware)
                    val sensors = readSensor(buffer, sensor)
                    _currentData = _currentData.copy(Hardwares = hardwares, Sensors = sensors, LastPollTime = System.currentTimeMillis())
                    _currentData
                }

                is Packet.PresentMonApps -> {
                    val appsCount = getByteBuffer(packet.data, LENGTH_SIZE, 0).short
                    val buffer = getByteBuffer(packet.data, appsCount.toInt() * NAME_SIZE, LENGTH_SIZE)
                    val apps = listOf("Auto") + readPresentMonApps(buffer, appsCount)
                    _currentData = _currentData.copy(PresentMonApps = apps, LastPollTime = System.currentTimeMillis())
                    _currentData
                }

                is Packet.SelectPresentMonApp -> null
                is Packet.SelectPollingRate -> null
            }
        }

    private fun readHardwareAndSensorCount(input: ByteArray): Pair<Int, Int> {
        val buffer = getByteBuffer(input, HEADER_SIZE, 0)
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

    private fun readPresentMonApps(buffer: ByteBuffer, count: Short): List<String> {
        return buildList {
            for (i in 0 until count) {
                add(buffer.readString(NAME_SIZE))
            }
        }
    }
}