package com.example.data

import kotlinx.coroutines.flow.Flow

class RomRepository(private val romDao: RomDao) {
    val allRoms: Flow<List<RomItem>> = romDao.getAllRoms()
    val allDrivers: Flow<List<DriverItem>> = romDao.getAllDrivers()

    suspend fun getRomById(id: Int): RomItem? = romDao.getRomById(id)

    suspend fun insertRom(rom: RomItem): Long = romDao.insertRom(rom)

    suspend fun updateRom(rom: RomItem) = romDao.updateRom(rom)

    suspend fun deleteRom(rom: RomItem) = romDao.deleteRom(rom)

    suspend fun insertDriver(driver: DriverItem) = romDao.insertDriver(driver)

    suspend fun insertDrivers(drivers: List<DriverItem>) = romDao.insertDrivers(drivers)

    suspend fun updateDriver(driver: DriverItem) = romDao.updateDriver(driver)
}
