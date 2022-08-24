package com.github.mislavmatijevic.nutrity.core.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.nio.ByteBuffer

/**
 * Static class that converts bitmaps to bytes and vice versa.
 * Done by following: https://stackoverflow.com/a/34165515
 */
class BitmapConverter {
    companion object {
        fun bitmapToBytes(bitmap: Bitmap): ByteArray {
            val size = bitmap.rowBytes * bitmap.height
            val byteBuffer = ByteBuffer.allocate(size)
            bitmap.copyPixelsToBuffer(byteBuffer)
            return byteBuffer.array()
        }

        fun bytesToBitmap(byteArray: ByteArray): Bitmap? {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }
}