package app.cleanmeter.core.common.reporting

object ApplicationParams {

    private var _isAutoStart = false
    val isAutostart: Boolean
        get() = _isAutoStart

    fun parse(args: Array<out String>) {
        _isAutoStart = args.contains("--autostart")
    }
}