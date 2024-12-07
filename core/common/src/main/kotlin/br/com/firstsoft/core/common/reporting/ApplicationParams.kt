package br.com.firstsoft.core.common.reporting

object ApplicationParams {

    private var _isAutoStart = false
    val isAutostart: Boolean
        get() = _isAutoStart

    private var _isVerbose = false
    val isVerbose: Boolean
        get() = _isVerbose

    fun parse(args: Array<out String>) {
        _isAutoStart = args.contains("--autostart")
        _isVerbose = args.contains("--verbose")
    }
}