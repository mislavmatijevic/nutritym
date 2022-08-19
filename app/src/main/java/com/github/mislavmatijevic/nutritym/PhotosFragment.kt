package com.github.mislavmatijevic.nutritym

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
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

    var savedPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        binding = FragmentPhotosBinding.inflate(inflater, container, false)

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

    @SuppressLint("SimpleDateFormat")
    private fun pictureTakenCallback(success: Boolean) {
        if (success && savedPhotoPath != null) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 4
            val bmpImage = BitmapFactory.decodeFile(savedPhotoPath, options)

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val jpgName = "IMG_${timeStamp}.jpg"
            val file = File(storageDir, jpgName)

            FileOutputStream(file).use { out ->
                bmpImage.compress(Bitmap.CompressFormat.JPEG, 50, out)
                out.flush()
                out.close()
            }

            if (file.exists()) {
                File(savedPhotoPath!!).delete()
                photos.add(0, Photo(file.absolutePath, bmpImage, "PhotoTestName", Date()))
                binding.rvPhotos.adapter?.notifyItemInserted(0)
            } else {
                Toast.makeText(
                    context,
                    "Unfortunately, photo wasn't saved due to unknown reason.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getFileUri(file: File): Uri =
        FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
}