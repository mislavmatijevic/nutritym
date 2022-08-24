package com.github.mislavmatijevic.nutritym.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mislavmatijevic.nutritym.database.entity.PhotoEntity

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getAll(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg photoEntities: PhotoEntity)

    @Query("SELECT * FROM photos WHERE date_taken > :earliestTime")
    fun getAllAfterTime(earliestTime: Long): List<PhotoEntity>
}