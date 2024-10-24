package hwinfo

enum class SensorReadingType(val value: Int) {
    None(0),
    Temp(1),
    Volt(2),
    Fan(3),
    Current(4),
    Power(5),
    Clock(6),
    Usage(7),
    Other(8),
    ;

    companion object {
        fun getByValue(value: Int): SensorReadingType = entries.firstOrNull { it.value == value } ?: None
    }
}