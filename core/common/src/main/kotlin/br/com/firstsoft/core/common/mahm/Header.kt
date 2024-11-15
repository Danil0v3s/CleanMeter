package br.com.firstsoft.core.common.mahm

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    /**
     * Allows applications to verify status of shared memory
     * MAHM: Hardware monitoring memory is initialised and contains valid data
     * 0xDEAD: Hardware monitoring memory is marked for deallocation and no longer contains valid data
     * Any other value means the memory isn't initialised
     */
    val dwSignature: Int,
    /**
     * mahm.Header Version ((major << 16) + minor)
     * Must be set to 0x00020000 for v2.0
     */
    val dwVersion: Int,
    /**
     * mahm.Header Size
     */
    val dwHeaderSize: Int,
    /**
     * Number of subsequent MAHM_SHARED_MEMORY_ENTRY entries
     */
    val dwNumEntries: Int,
    /**
     * Size of each entry in subsequent MAHM_SHARED_MEMORY_ENTRY array
     */
    val dwEntrySize: Int,
    /**
     * Number of subsequent MAHM_SHARED_MEMORY_GPU_ENTRY entries
     */
    val dwNumGpuEntries: Int,
    /**
     * Size of each entry in subsequent MAHM_SHARED_MEMORY_GPU_ENTRY array
     */
    val dwGpuEntrySize: Int,
    val lastCheck: Int
) {
    companion object Signatures {
        const val INITIALISED = "MAHM"
        const val DEAD = "0xDEAD"
    }

    val totalSize: Int
        get() = (dwNumEntries * dwEntrySize) + (dwNumGpuEntries * dwGpuEntrySize) + dwHeaderSize
}
