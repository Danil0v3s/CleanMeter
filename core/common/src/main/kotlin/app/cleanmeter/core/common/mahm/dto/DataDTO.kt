package app.cleanmeter.core.common.mahm.dto

import kotlinx.serialization.Serializable
import app.cleanmeter.core.common.mahm.Data
import app.cleanmeter.core.common.mahm.Entry
import app.cleanmeter.core.common.mahm.SourceID

@Serializable
data class DataDTO(
    val entries: Map<SourceID, EntryDTO>
) {
    @Serializable
    data class EntryDTO(
        val data: Float,
        val minLimit: Float,
        val maxLimit: Float,
    )
}

fun Data.toDTO(): DataDTO {
    return DataDTO(
        entries = entries.associate { it.dwSrcId to it.toDTO() }
    )
}

fun Entry.toDTO(): DataDTO.EntryDTO {
    return DataDTO.EntryDTO(
        data = data,
        minLimit = minLimit,
        maxLimit = maxLimit,
    )
}
