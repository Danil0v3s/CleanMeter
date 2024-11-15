package br.com.firstsoft.core.common.mahm

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    /**
     * mahm.Data source name
     * (eg: "Core Clock")
     */
    val szSrcName: String,
    /**
     * mahm.Data source units
     * (eg: "MHz")
     */
    val szSrcUnits: String,
    /**
     * Localised data source name
     * (eg: "„астота ¤дра" for Russian GUI)
     */
    val szLocalisedSrcName: String,
    /**
     * Localised data source units
     * (eg: "ћ√ц" for Russian GUI)
     */
    val szLocalisedSrcUnits: String,
    /**
     * Recommended output format
     * (eg: "%.3f" for "Core voltage" data source)
     */
    val szRecommendedFormat: String,
    /**
     * Last polled data
     * (eg: 500MHz)
     * This field can be set to FLT_MAX if data is not available at the moment
     */
    val data: Float,
    /**
     * Minimum limit for graphs
     * (eg: 0MHz)
     */
    val minLimit: Float,
    /**
     * Maximum limit for graphs
     * (eg: 2000MHz)
     */
    val maxLimit: Float,
    /**
     * Bitmask containing combination of MAHM_SHARED_MEMORY_FLAG
     */
    val dwFlags: EntryFlag,
    /**
     * mahm.Data source GPU index (zero based) or 0xFFFFFFFF for global data sources (eg: Framerate)
     */
    val dwGpu: Int,
    val dwSrcId: SourceID
) {
    override fun toString() = "$szSrcName = $data$szSrcUnits"
}
