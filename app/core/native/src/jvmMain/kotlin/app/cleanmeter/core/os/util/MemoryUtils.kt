package app.cleanmeter.core.os.util

import com.sun.jna.Pointer
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*

actual fun getByteBuffer(input: InputStream, length: Int): ByteBuffer {
    if (length <= 0) return ByteBuffer.allocate(0).order(ByteOrder.LITTLE_ENDIAN)
    return ByteBuffer.wrap(input.readNBytes(length)).order(ByteOrder.LITTLE_ENDIAN)
}

actual fun getByteBuffer(input: ByteArray, length: Int, offset: Int): ByteBuffer {
    if (length <= 0) return ByteBuffer.allocate(0)
    return ByteBuffer.wrap(input).slice(offset, length).order(ByteOrder.LITTLE_ENDIAN)
}

actual fun getByteBuffer(pointer: Any, size: Int, offset: Int): ByteBuffer {
    require(pointer is com.sun.jna.Pointer) { "Expected JNA Pointer but got ${pointer::class}" }
    val buffer = ByteBuffer.allocateDirect(size)
    buffer.put(pointer.getByteArray(0, size))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.rewind()
    buffer.position(offset)

    return buffer
}

actual val systemCharset: Charset by lazy {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    when {
        "win" in osName -> Charset.forName(System.getProperty("sun.jnu.encoding"))
        "mac" in osName -> Charset.forName(System.getenv("LC_CTYPE") ?: "UTF-8")
        else -> Charset.forName(System.getenv("LANG")?.substringAfter('.') ?: "UTF-8")
    }
}

actual fun ByteBuffer.readString(maxLength: Int, charset: Charset): String {
    val array = ByteArray(maxLength)
    get(array, 0, maxLength)

    return String(trim(array), charset)
}

actual fun trim(bytes: ByteArray): ByteArray {
    var i = bytes.size - 1
    while (i >= 0 && bytes[i].toInt() == 0) {
        --i
    }
    return bytes.copyOf(i + 1)
}