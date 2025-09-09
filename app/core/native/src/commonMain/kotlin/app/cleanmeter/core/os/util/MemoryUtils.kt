package app.cleanmeter.core.os.util

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset

expect fun getByteBuffer(input: InputStream, length: Int): ByteBuffer
expect fun getByteBuffer(input: ByteArray, length: Int, offset: Int): ByteBuffer
expect fun getByteBuffer(pointer: Any, size: Int, offset: Int = 0): ByteBuffer

expect val systemCharset: Charset

expect fun ByteBuffer.readString(maxLength: Int, charset: Charset = systemCharset): String
expect fun trim(bytes: ByteArray): ByteArray
