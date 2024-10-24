package util

import com.sun.jna.Pointer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

internal fun getByteBuffer(pointer: Pointer, size: Int, offset: Int = 0): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(size)
    buffer.put(pointer.getByteArray(0, size))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.rewind()
    buffer.position(offset)

    return buffer
}

internal fun ByteBuffer.readString(maxLength: Int, charset: Charset = StandardCharsets.ISO_8859_1): String {
    val array = ByteArray(maxLength)
    get(array, 0, maxLength)

    return String(trim(array), charset)
}

internal fun trim(bytes: ByteArray): ByteArray {
    var i = bytes.size - 1
    while (i >= 0 && bytes[i].toInt() == 0) {
        --i
    }
    return bytes.copyOf(i + 1)
}