package com.github.mislavmatijevic.nutritym

import android.graphics.Bitmap
import java.io.Serializable
import java.util.*

data class Photo(
    val path: String,
    val bitmap: Bitmap,
    val name: String,
    val dateTaken: Date
) : Serializable
