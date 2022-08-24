package com.github.mislavmatijevic.nutritym.database.mapper

import android.util.Base64
import com.github.mislavmatijevic.nutrity.core.converter.BitmapConverter
import com.github.mislavmatijevic.nutrity.core.mapper.GenericMapper
import com.github.mislavmatijevic.nutrity.core.model.Photo
import com.github.mislavmatijevic.nutritym.database.entity.PhotoEntity

/**
 * Maps photos (models) to photos (entities).
 * It simplifies translation of database's photo entities into application's photo objects.
 */
class PhotoMapper : GenericMapper<Photo, PhotoEntity> {
    override fun mapEntity(entity: PhotoEntity): Photo {
        return Photo(
            entity.name,
            BitmapConverter.bytesToBitmap(Base64.decode(entity.bitmapBase64, Base64.NO_WRAP))!!,
            entity.name,
            entity.dateTaken
        )
    }

    override fun map(dto: Photo): PhotoEntity {
        return PhotoEntity(
            0,
            dto.name,
            Base64.encodeToString(BitmapConverter.bitmapToBytes(dto.bitmap), Base64.NO_WRAP),
            dto.dateTaken
        )
    }
}