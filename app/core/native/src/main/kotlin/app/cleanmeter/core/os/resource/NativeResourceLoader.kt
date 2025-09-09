package app.cleanmeter.core.os.resource

object NativeResourceLoader {
    fun load(path: String): String {
        return NativeResourceLoader::class.java.getResourceAsStream(path)
            ?.bufferedReader()
            .use { it?.readText().orEmpty() }
    }
}