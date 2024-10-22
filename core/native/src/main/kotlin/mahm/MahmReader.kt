package mahm

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinNT
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import mahm.MAHMSizes.MAX_STRING_LENGTH
import win32.WindowsService
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

object MAHMSizes {
    const val HEADER_SIZE = 32
    const val MAX_STRING_LENGTH = 260
}

private const val MEMORY_MAP_FILE_NAME = "MAHMSharedMemory"

class MahmReader {

    private val windowsService = WindowsService()
    private var pollingJob: Job? = null

    private var memoryMapFile: WinNT.HANDLE? = null
    private var pointer: Pointer? = null

    var pollingInterval = 1000L
    val currentData = flow<Data?> {
        tryOpenMemoryFile()
        pointer ?: return@flow

        while (true) {
            try {
                emit(readData(pointer!!))
                delay(pollingInterval)
            } catch (e: CancellationException) {
                break
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        pointer?.let { windowsService.unmapViewOfFile(it) }
        memoryMapFile?.let { windowsService.closeHandle(it) }
    }

    fun tryOpenMemoryFile() {
        windowsService.openMemoryMapFile(MEMORY_MAP_FILE_NAME)?.let { handle ->
            memoryMapFile = handle
            pointer = windowsService.mapViewOfFile(handle) ?: throw Error("Something went wrong: Could not create pointer")
        } ?: throw Error("Could not read AfterBurner data. Is it running?")
    }

    private fun readHeader(pointer: Pointer): Header {
        val buffer = getByteBuffer(pointer, MAHMSizes.HEADER_SIZE)

        return readHeader(buffer)
    }

    private fun readData(pointer: Pointer): Data {
        val header = readHeader(pointer)
        val buffer = getByteBuffer(pointer, header.totalSize, header.dwHeaderSize)
        val entries = readCpuEntries(buffer, header.dwNumEntries)
        val gpuEntries = readGpuEntries(buffer, header.dwNumGpuEntries)

        return Data(
            header = header,
            entries = entries,
            gpuEntries = gpuEntries
        )
    }

    private fun getByteBuffer(pointer: Pointer, size: Int, skip: Int = 0): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(size)
        buffer.put(pointer.getByteArray(0, size))
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.rewind()
        buffer.position(skip)

        return buffer
    }

    private fun readHeader(buffer: ByteBuffer) = Header(
        dwSignature = buffer.int,
        dwVersion = buffer.int,
        dwHeaderSize = buffer.int,
        dwNumEntries = buffer.int,
        dwEntrySize = buffer.int,
        lastCheck = buffer.int,
        dwNumGpuEntries = buffer.int,
        dwGpuEntrySize = buffer.int
    )

    private fun readGpuEntries(buffer: ByteBuffer, numEntries: Int) = buildList {
        for (i in 0 until numEntries) {
            add(
                GPUEntry(
                    szGpuId = buffer.readString(),
                    szFamily = buffer.readString(),
                    szDevice = buffer.readString(),
                    szDriver = buffer.readString(),
                    szBios = buffer.readString(),
                    dwMemAmount = buffer.int
                )
            )
        }
    }

    private fun readCpuEntries(buffer: ByteBuffer, numEntries: Int) = buildList {
        for (i in 0 until numEntries) {
            add(
                Entry(
                    szSrcName = buffer.readString(),
                    szSrcUnits = buffer.readString(),
                    szLocalisedSrcName = buffer.readString(),
                    szLocalisedSrcUnits = buffer.readString(),
                    szRecommendedFormat = buffer.readString(),
                    data = buffer.float,
                    minLimit = buffer.float,
                    maxLimit = buffer.float,
                    dwFlags = EntryFlag.fromInt(buffer.int),
                    dwGpu = buffer.int,
                    dwSrcId = SourceID.fromInt(buffer.int)
                )
            )
        }
    }

    private fun ByteBuffer.readString(): String {
        val array = ByteArray(MAX_STRING_LENGTH)
        get(array, 0, MAX_STRING_LENGTH)

        return String(trim(array), StandardCharsets.ISO_8859_1)
    }

    private fun trim(bytes: ByteArray): ByteArray {
        var i = bytes.size - 1
        while (i >= 0 && bytes[i].toInt() == 0) {
            --i
        }
        return bytes.copyOf(i + 1)
    }
}
