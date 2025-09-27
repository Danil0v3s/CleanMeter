package app.cleanmeter.core.os.resource

actual object NativeResourceLoader {
    actual fun load(path: String): String {
        return NativeResourceLoader::class.java.getResourceAsStream(path)
            ?.bufferedReader()
            .use { it?.readText().orEmpty() }
    }
}