package com.github.mislavmatijevic.nutritym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mislavmatijevic.nutrity.core.model.Photo
import com.github.mislavmatijevic.nutritym.databinding.FragmentPhotoCommentsBinding

const val ARG_PHOTO: String = "param_passed_photo"

class PhotoCommentFragment : Fragment() {

    private lateinit var binding: FragmentPhotoCommentsBinding
    private var selectedPhoto: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPhoto = it.getSerializable(ARG_PHOTO) as Photo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoCommentsBinding.inflate(layoutInflater)

        if (selectedPhoto != null) {
            binding.apply {
                imageViewBig.setImageBitmap(selectedPhoto!!.bitmap)
                tvName.text = selectedPhoto!!.name
            }
        } else {
            Toast.makeText(
                context,
                "Something went wrong with displaying selected photo. Please try again later.",
                Toast.LENGTH_LONG
            ).show()
        }

        return binding.root
    }
}