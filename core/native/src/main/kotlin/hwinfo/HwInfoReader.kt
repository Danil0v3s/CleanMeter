package hwinfo

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinNT
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import util.getByteBuffer
import util.readString
import win32.WindowsService
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.cancellation.CancellationException

private const val MEMORY_MAP_FILE_NAME = "Global\\HWiNFO_SENS_SM2"
private const val SENSOR_STRING_LEN = 128
private const val UNIT_STRING_LEN = 16
private const val HEADER_SIZE = 44

class HwInfoReader {

    private val windowsService = WindowsService()
    private var memoryMapFile: WinNT.HANDLE? = null
    private var pointer: Pointer? = null

    var pollingInterval = 200L
    val currentData = flow {
        tryOpenMemoryFile()
        pointer?.let { pointer ->
            val header = readHeader(pointer)
            val sensors = readSensors(pointer, header)
            val data = HwInfoData(
                header = header,
                sensors = sensors,
                readings = emptyList(),
            )

            while (true) {
                try {
                    emit(data.copy(readings = readSensorReading(pointer, header)))
                    delay(pollingInterval)
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

    private fun readHeader(pointer: Pointer): SensorSharedMem {
        val buffer = getByteBuffer(pointer, HEADER_SIZE)

        return SensorSharedMem(
            dwSignature = buffer.int,
            dwVersion = buffer.int,
            dwRevision = buffer.int,
            pollTime = buffer.long,
            dwOffsetOfSensorSection = buffer.int,
            dwSizeOfSensorElement = buffer.int,
            dwNumSensorElements = buffer.int,
            dwOffsetOfReadingSection = buffer.int,
            dwSizeOfReadingElement = buffer.int,
            dwNumReadingElements = buffer.int
        )
    }

    private fun readSensors(pointer: Pointer, header: SensorSharedMem): List<SensorElement> {
        return buildList {
            for (i in 0 until header.dwNumSensorElements) {
                val buffer = pointer.getByteArray(
                    header.dwOffsetOfSensorSection + (header.dwSizeOfSensorElement.toLong() * i),
                    header.dwSizeOfSensorElement
                ).let {
                    ByteBuffer.wrap(it).order(
                        ByteOrder.LITTLE_ENDIAN
                    )
                }

                add(
                    SensorElement(
                        dwSensorId = buffer.int,
                        dwSensorInst = buffer.int,
                        szSensorNameOrig = buffer.readString(SENSOR_STRING_LEN),
                        szSensorNameUser = buffer.readString(SENSOR_STRING_LEN)
                    )
                )
            }
        }
    }

    private fun readSensorReading(pointer: Pointer, header: SensorSharedMem): List<SensorReadingElement> {
        return buildList {
            for (i in 0 until header.dwNumReadingElements) {
                val buffer = pointer.getByteArray(
                    header.dwOffsetOfReadingSection + (header.dwSizeOfReadingElement.toLong() * i),
                    header.dwSizeOfReadingElement
                ).let {
                    ByteBuffer.wrap(it).order(
                        ByteOrder.LITTLE_ENDIAN
                    )
                }

                add(
                    SensorReadingElement(
                        readingType = SensorReadingType.getByValue(buffer.int),
                        dwSensorIndex = buffer.int,
                        dwReadingID = buffer.int,
                        szLabelOrig = buffer.readString(SENSOR_STRING_LEN),
                        szLabelUser = buffer.readString(SENSOR_STRING_LEN),
                        szUnit = buffer.readString(UNIT_STRING_LEN),
                        value = buffer.double,
                        valueMin = buffer.double,
                        valueMax = buffer.double,
                        valueAvg = buffer.double
                    )
                )
            }
        }
    }
}