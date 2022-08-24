package com.github.mislavmatijevic.nutrity.core.data

import com.github.mislavmatijevic.nutrity.core.model.Photo

/**
 * Gateway to photo fetching without actual implementation dependencies.
 */
interface PhotoDataSource {
    fun getAll(): List<Photo>

    fun insertAll(vararg photos: Photo)

    fun getAllAfterTime(earliestTime: Long): List<Photo>
}