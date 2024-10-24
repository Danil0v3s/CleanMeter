package util

import com.sun.jna.Pointer
import mahm.MAHMSizes.MAX_STRING_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

internal fun getByteBuffer(pointer: Pointer, size: Int, skip: Int = 0): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(size)
    buffer.put(pointer.getByteArray(0, size))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.rewind()
    buffer.position(skip)

    return buffer
}

internal fun ByteBuffer.readString(): String {
    val array = ByteArray(MAX_STRING_LENGTH)
    get(array, 0, MAX_STRING_LENGTH)

    return String(trim(array), StandardCharsets.ISO_8859_1)
}

internal fun trim(bytes: ByteArray): ByteArray {
    var i = bytes.size - 1
    while (i >= 0 && bytes[i].toInt() == 0) {
        --i
    }
    return bytes.copyOf(i + 1)
}