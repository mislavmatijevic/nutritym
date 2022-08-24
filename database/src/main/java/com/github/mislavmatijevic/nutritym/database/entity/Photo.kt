package com.github.mislavmatijevic.nutritym.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey val id: Int,

    val name: String,
    @ColumnInfo(name = "bitmap_base64") val bitmapBase64: String,
    @ColumnInfo(name = "date_taken") val dateTaken: Date
)