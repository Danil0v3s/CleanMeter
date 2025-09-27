package app.cleanmeter.core.os.resource

expect object NativeResourceLoader {
    fun load(path: String): String
}
