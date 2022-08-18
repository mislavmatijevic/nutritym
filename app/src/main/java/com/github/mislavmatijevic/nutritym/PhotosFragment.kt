package com.github.mislavmatijevic.nutritym

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.mislavmatijevic.nutritym.databinding.FragmentPhotosBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)

        binding.btnTakePhoto.setOnClickListener {
            val newImageFileUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                createImageFile()
            )

            takePicture.launch(newImageFileUri)
        }

        return binding.root
    }

    var currentPhotoPath = ""

    val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                Toast.makeText(context, "Photo saved", Toast.LENGTH_LONG)
            }
        }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "IMG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = "content:" + absolutePath
        }
    }

}