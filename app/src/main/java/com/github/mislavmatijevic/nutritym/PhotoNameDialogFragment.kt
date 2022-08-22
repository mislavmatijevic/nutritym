package com.github.mislavmatijevic.nutritym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.DialogFragment
import com.github.mislavmatijevic.nutritym.databinding.DialogGivePhotoNameBinding

class PhotoNameDialogFragment(private val callback: (String) -> Unit) : DialogFragment() {

    private lateinit var binding: DialogGivePhotoNameBinding

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding =
                DialogGivePhotoNameBinding.inflate(requireActivity().layoutInflater, null, false)

            builder.setView(binding.root)
                .setPositiveButton(R.string.accept) { _, _ ->
                    val newPhotoName = binding.etPhotoName.text.toString().trim()
                    if (!TextUtils.isEmpty(newPhotoName)) {
                        callback(newPhotoName)
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}