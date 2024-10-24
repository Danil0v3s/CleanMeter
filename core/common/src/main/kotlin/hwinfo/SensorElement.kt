package hwinfo

data class SensorElement(
    val dwSensorId: Int,
    val dwSensorInst: Int,
    val szSensorNameOrig: String,
    val szSensorNameUser: String,
)