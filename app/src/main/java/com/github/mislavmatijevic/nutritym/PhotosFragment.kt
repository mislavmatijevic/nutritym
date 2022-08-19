package com.github.mislavmatijevic.nutritym

import android.annotation.SuppressLint
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding
    private var photos: ArrayList<Photo> = ArrayList<Photo>()
    private lateinit var storageDir: File
    var exifDateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)

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
                val newFile = createImageFile()
                savedPhotoPath = newFile.path
                takePicture.launch(getFileUri(newFile))
            }

            return root
        }
    }

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

            photos.reverse()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        return File.createTempFile(
            "temp_",
            ".bmp",
            storageDir
        )
    }

    val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            pictureTakenCallback(success)
        }

    private fun pictureTakenCallback(success: Boolean) {
        var imageSaved = false

        if (success) {
            imageSaved = editImageBeforeCompressing()
        }

        if (!imageSaved) {
            Toast.makeText(
                context,
                "Unfortunately, photo wasn't saved. Please, try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun editImageBeforeCompressing(): Boolean {
        var success = false

        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        var bmpImage = BitmapFactory.decodeFile(savedPhotoPath, options)

        if (bmpImage != null) {
            val exifOriginalBmpImage = ExifInterface(savedPhotoPath!!)
            bmpImage = fixImageOrientation(bmpImage, exifOriginalBmpImage)

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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
     * @author Jason Robinson at https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     */
    fun rotateImage(source: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, false
        )
    }

    private fun getFileUri(file: File): Uri =
        FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
}