package com.github.mislavmatijevic.nutritym

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mislavmatijevic.nutritym.databinding.FragmentPhotosBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * This fragment displays all photos user has stored locally on the device in the app's "files/Pictures".
 */
class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding
    private var photos: ArrayList<Photo> = ArrayList<Photo>()
    private lateinit var storageDir: File
    var exifDateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)

    companion object {
        const val PHOTO_PATH_TAG = "savedPhotoPath"
    }

    private var savedPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)

        storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        loadLocalImages()

        binding.apply {

            rvPhotos.adapter = PhotosAdapter(photos)
            rvPhotos.layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

            btnTakePhoto.setOnClickListener {
                val newFile = createTempImageFile()
                savedPhotoPath = newFile.path
                takePicture.launch(getFileUri(newFile))
            }

            if (savedInstanceState != null) {
                savedPhotoPath = savedInstanceState.getString(PHOTO_PATH_TAG)
            }

            return root
        }
    }

    /**
     * Save path variable before activity destruction.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(PHOTO_PATH_TAG, savedPhotoPath)
        super.onSaveInstanceState(outState)
    }

    /**
     * Loads local images into the app.
     * Without this method, every time the activity would restart, all the images would be gone.
     */
    private fun loadLocalImages() {

        storageDir.listFiles { dir, name ->
            name.endsWith(".jpg", true)
        }?.forEach { photo ->

            var photoTakenDateTime = Date(photo.lastModified())
            try {
                val exifDateTime = ExifInterface(photo).getAttribute(ExifInterface.TAG_DATETIME)!!
                photoTakenDateTime = exifDateFormat.parse(exifDateTime) ?: photoTakenDateTime
            } catch (ex: Exception) {
                Log.e("LocalDates", "Local dates not loaded")
                ex.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Could not correctly load dates for locally stored files!",
                    Toast.LENGTH_LONG
                ).show()
            }

            photos.add(
                Photo(
                    photo.absolutePath,
                    BitmapFactory.decodeFile(photo.absolutePath),
                    "Loaded file",
                    photoTakenDateTime
                )
            )

            photos.sortWith { photo1, photo2 ->
                photo2.dateTaken.compareTo(photo1.dateTaken)
            }
        }
    }

    /**
     * Creates a simple temporary bmp file for storing raw image directly from the camera.
     * BMP is created in order to immediately resize it and compress it into JPG at 50% quality.
     */
    private fun createTempImageFile(): File {
        return File.createTempFile(
            "temp_",
            ".bmp",
            storageDir
        )
    }

    /**
     * This callback controls what happens immediately AFTER camera activity sends back a photo.
     * What it does is call a function for resizing and rotating the taken image.
     * It knows if the tasks fails and displays a warning toast message in such case.
     */
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val imageSaved = editImageBeforeCompressing()
                if (!imageSaved) {
                    Toast.makeText(
                        context,
                        "Unfortunately, photo wasn't saved. Please, try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    /**
     * Makes sure the taken photo is not falsly oriented and 5MB in size.
     * It takes a raw BMP file and locally saves a 30-70KB JPG image correctly oriented.
     */
    private fun editImageBeforeCompressing(): Boolean {
        var success = false

        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        var bmpImage = BitmapFactory.decodeFile(savedPhotoPath, options)

        if (bmpImage != null) {
            val exifOriginalBmpImage = ExifInterface(savedPhotoPath!!)
            bmpImage = fixImageOrientation(bmpImage, exifOriginalBmpImage)

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val jpgName = "IMG_${timeStamp}.jpg"
            val file = File(storageDir, jpgName)

            FileOutputStream(file).use { out ->
                bmpImage.compress(Bitmap.CompressFormat.JPEG, 50, out)
                out.flush()
                out.close()
            }

            val exifDate = copyExifDateTimeData(file, exifOriginalBmpImage)

            if (file.exists()) {
                File(savedPhotoPath!!).delete()
                photos.add(0, Photo(file.absolutePath, bmpImage, "PhotoTestName", exifDate))
                binding.rvPhotos.adapter?.notifyItemInserted(0)
                success = true
            }
        }

        return success
    }

    /**
     * Takes an original raw BMP file and copies Exif "DATETIME" property into a new JPG file.
     * This is done to ensure every image taken using this app always knows exactly when it was made.
     */
    private fun copyExifDateTimeData(
        file: File,
        exifOriginalBmpImage: ExifInterface
    ): Date {
        val exifNewJpgImage = ExifInterface(file)
        val originalTimeTaken = exifOriginalBmpImage.getAttribute(ExifInterface.TAG_DATETIME)!!
        exifNewJpgImage.setAttribute(ExifInterface.TAG_DATETIME, originalTimeTaken)
        exifNewJpgImage.saveAttributes()
        return exifDateFormat.parse(originalTimeTaken)!!
    }

    /**
     * Fixes orientation of an image by examining an "ORIENTATION" tag in its Exif.
     * For whatever reason, camera activity rotates an image even when the UI is in portrait mode.
     * @author Jason Robinson at https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     */
    private fun fixImageOrientation(bitmapFile: Bitmap, ei: ExifInterface): Bitmap {
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmapFile, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmapFile, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmapFile, 270)
            else -> bitmapFile
        }
    }

    /**
     * This method performs the rotation of a BMP image in specified angle.
     * @author Jason Robinson at https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     */
    private fun rotateImage(source: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, false
        )
    }

    /**
     * Retrieves a file Uri. Used when writing an image into an already prepared local temp file.
     */
    private fun getFileUri(file: File): Uri =
        FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
}