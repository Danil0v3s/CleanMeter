package br.com.firstsoft.core.os.hardwaremonitor

import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.os.util.getByteBuffer
import br.com.firstsoft.core.os.util.readString
import br.com.firstsoft.core.os.win32.WindowsService
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinNT
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.cancellation.CancellationException

private const val MEMORY_MAP_FILE_NAME = "CleanMeterHardwareMonitor"
private const val HARDWARE_SIZE = 260
private const val SENSOR_SIZE = 392
private const val NAME_SIZE = 128
private const val IDENTIFIER_SIZE = 128
private const val HEADER_SIZE = 8

object HardwareMonitorReader {
    private val windowsService = WindowsService()
    private var memoryMapFile: WinNT.HANDLE? = null
    private var pointer: Pointer? = null

    val currentData = flow {
        while (pointer == null) {
            tryOpenMemoryFile()
            delay(2000L)
        }
        pointer?.let { pointer ->
            while (true) {
                try {
                    val (hardwareCount, sensorCount) = readHardwareAndSensorCount(pointer)
                    val hardwares = readHardware(pointer, hardwareCount)
                    val sensors = readSensor(pointer, sensorCount, hardwareCount)
                    emit(HardwareMonitorData(0L, hardwares, sensors))
                    delay(500L)
                } catch (e: CancellationException) {
                    break
                }
            }
        }
    }

    private fun tryOpenMemoryFile() {
        if (memoryMapFile == null) {
            windowsService.openMemoryMapFile(MEMORY_MAP_FILE_NAME)?.let { handle ->
                memoryMapFile = handle
                pointer = windowsService.mapViewOfFile(handle)
            }
        }
    }

    private fun readHardwareAndSensorCount(pointer: Pointer): Pair<Int, Int> {
        val buffer = getByteBuffer(pointer, HEADER_SIZE)
        return buffer.int to buffer.int
    }

    private fun readHardware(pointer: Pointer, count: Int): List<HardwareMonitorData.Hardware> {
        return buildList {
            for (i in 0 until count) {
                val buffer = pointer.getByteArray(
                    HEADER_SIZE + (HARDWARE_SIZE.toLong() * i), HARDWARE_SIZE
                ).let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN) }

                val hardware = HardwareMonitorData.Hardware(
                    Name = buffer.readString(NAME_SIZE),
                    Identifier = buffer.readString(IDENTIFIER_SIZE),
                    HardwareType = HardwareMonitorData.HardwareType.fromValue(buffer.int),
                )
                add(hardware)
            }
        }
    }

    private fun readSensor(pointer: Pointer, count: Int, hardwareCount: Int): List<HardwareMonitorData.Sensor> {
        return buildList {
            for (i in 0 until count) {
                val buffer = pointer.getByteArray(
                    HEADER_SIZE + hardwareCount * HARDWARE_SIZE + (SENSOR_SIZE.toLong() * i), SENSOR_SIZE
                ).let { ByteBuffer.wrap(it).order(ByteOrder.LITTLE_ENDIAN) }

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