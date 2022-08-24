package com.github.mislavmatijevic.nutritym.database.data

import android.content.Context
import com.github.mislavmatijevic.nutrity.core.data.PhotoDataSource
import com.github.mislavmatijevic.nutrity.core.model.Photo
import com.github.mislavmatijevic.nutritym.database.NutritymDatabase
import com.github.mislavmatijevic.nutritym.database.mapper.PhotoMapper

/**
 * Takes care of providing photos from the local database.
 * @param context Required to be a real active context!
 */
class DbDataSource(context: Context) : PhotoDataSource {

    private val photoDAO = NutritymDatabase.getDatabase(context)!!.getPhotoDao()

    override fun getAll(): List<Photo> = photoDAO.getAll().map {
        PhotoMapper().mapEntity(it)
    }

    override fun insertAll(vararg photos: Photo) {
        val photoEntities = photos.map {
            PhotoMapper().map(it)
        }
        photoDAO.insertAll(*photoEntities.toTypedArray())
    }

    override fun getAllAfterTime(earliestTime: Long): List<Photo> =
        photoDAO.getAllAfterTime(earliestTime).map {
            PhotoMapper().mapEntity(it)
        }
}