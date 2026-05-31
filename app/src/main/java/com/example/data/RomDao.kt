package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RomDao {
    @Query("SELECT * FROM rom_items ORDER BY timestamp DESC")
    fun getAllRoms(): Flow<List<RomItem>>

    @Query("SELECT * FROM rom_items WHERE id = :id LIMIT 1")
    suspend fun getRomById(id: Int): RomItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRom(rom: RomItem): Long

    @Update
    suspend fun updateRom(rom: RomItem)

    @Delete
    suspend fun deleteRom(rom: RomItem)

    @Query("SELECT * FROM gpu_drivers ORDER BY version DESC")
    fun getAllDrivers(): Flow<List<DriverItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverItem>)

    @Update
    suspend fun updateDriver(driver: DriverItem)
}
