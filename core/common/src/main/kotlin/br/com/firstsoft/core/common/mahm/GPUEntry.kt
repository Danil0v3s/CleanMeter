package br.com.firstsoft.core.common.mahm

import kotlinx.serialization.Serializable

@Serializable
data class GPUEntry(
    /**
     * GPU identifier represented in VEN_%04X&DEV_%04X&SUBSYS_%08X&REV_%02X&BUS_%d&DEV_%d&FN_%d format
     * (eg: VEN_10DE&DEV_0A20&SUBSYS_071510DE&BUS_1&DEV_0&FN_0)
     */
    val szGpuId: String,
    /**
     * GPU family (Can be empty if data is not available)
     * (eg: GT216)
     */
    val szFamily: String,
    /**
     * Display device description (Can be empty if data is not available)
     * (eg: GeForce GT 220)
     */
    val szDevice: String,
    /**
     * Display driver description (Can be empty if data is not available)
     * (eg: 6.14.11.9621, ForceWare 196.21)
     */
    val szDriver: String,
    /**
     * BIOS version (Can be empty if data is not available)
     * (eg: 70.16.24.00.00)
     */
    val szBios: String,
    /**
     * On-board memory amount in KB (Can be empty if data is not available)
     * (eg: 1048576)
     */
    val dwMemAmount: Int
)
