package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rom_items")
data class RomItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val originalPath: String,
    val originalFormat: String, // "NSZ", "XCI", "NSP", "GZ", "ZIP"
    val targetFormat: String,   // "NSP (Pronto para Android)"
    val sizeBytes: Long,
    val titleId: String,        // e.g. "0100F2C0115B6000"
    val firmwareRequired: String, // e.g. "17.0.0"
    val timestamp: Long = System.currentTimeMillis(),
    val isOptimized: Boolean = false,
    val isConverted: Boolean = false,
    val emulatorTarget: String = "Yuzu / Sudachi",
    val customNotes: String = ""
)

@Entity(tableName = "gpu_drivers")
data class DriverItem(
    @PrimaryKey val id: String, // e.g. "turnip-v24.1.0-r17"
    val name: String,
    val version: String,
    val developer: String,
    val releaseDate: String,
    val compatibleGpus: String, // e.g. "Adreno 730, 740, 750"
    val downloadUrl: String,
    val description: String,
    val sizeMb: Double,
    val isDownloaded: Boolean = false,
    val localPath: String = ""
)
