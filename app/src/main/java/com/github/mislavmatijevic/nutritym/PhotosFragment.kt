package com.github.mislavmatijevic.nutritym

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mislavmatijevic.nutritym.databinding.FragmentPhotosBinding

class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)

        return binding.root
    }
}