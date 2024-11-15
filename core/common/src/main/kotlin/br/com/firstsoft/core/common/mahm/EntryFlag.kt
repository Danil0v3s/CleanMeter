package br.com.firstsoft.core.common.mahm

import kotlinx.serialization.Serializable

@Serializable
enum class EntryFlag(val value: Int) {
    None(0),
    MAHM_SHARED_MEMORY_ENTRY_FLAG_SHOW_IN_OSD(0x00000001),
    MAHM_SHARED_MEMORY_ENTRY_FLAG_SHOW_IN_LCD(0x00000002),
    MAHM_SHARED_MEMORY_ENTRY_FLAG_SHOW_IN_TRAY(0x00000004);

    companion object {
        fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: None
    }
}
