package hwinfo

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinNT
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import util.getByteBuffer
import win32.WindowsService
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val MEMORY_MAP_FILE_NAME = "Global\\HWiNFO_SENS_SM2"
private const val SENSOR_STRING_LEN = 128
private const val UNIT_STRING_LEN = 16
private const val HEADER_SIZE = 40

class HwInfoReader {

    private val windowsService = WindowsService()
    private var pollingJob: Job? = null
    private var memoryMapFile: WinNT.HANDLE? = null
    private var pointer: Pointer? = null

    var pollingInterval = 1000L
//    val currentData = flow<Unit> {
//
//    }

    init {
        tryOpenMemoryFile()
        pointer?.let { pointer ->
            val header = readHeader(pointer)
            header.pollTime
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
            pollTime = buffer.int,
            dwOffsetOfSensorSection = buffer.int,
            dwSizeOfSensorElement = buffer.int,
            dwNumSensorElements = buffer.int,
            dwOffsetOfReadingSection = buffer.int,
            dwdSizeOfReadingElement = buffer.int,
            dwNumReadingElements = buffer.int
        )
    }
}