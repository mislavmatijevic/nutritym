package com.github.mislavmatijevic.nutritym.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.mislavmatijevic.nutritym.database.dao.PhotoDao
import com.github.mislavmatijevic.nutritym.database.entity.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 2, exportSchema = false)
abstract class NutritymDatabase : RoomDatabase() {

    abstract fun getPhotoDao(): PhotoDao

    companion object {
        @Volatile
        private var instance: NutritymDatabase? = null

        fun getDatabase(context: Context): NutritymDatabase? {
            return instance ?: synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutritymDatabase::class.java,
                    "nutrity_database"
                ).build()
                return instance
            }
        }
    }
}