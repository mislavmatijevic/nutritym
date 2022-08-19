package com.github.mislavmatijevic.nutritym

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mislavmatijevic.nutritym.databinding.PhotoListItemBinding
import java.text.SimpleDateFormat
import java.util.*

class PhotosAdapter(
    private val photos: List<Photo>,
    private val longTapCallback: (Photo) -> Boolean
) :
    RecyclerView.Adapter<PhotosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.photo_list_item, parent, false)
        return PhotosViewHolder(view, longTapCallback)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size
}

class PhotosViewHolder(private val view: View, private val longTapCallback: (Photo) -> Boolean) :
    RecyclerView.ViewHolder(view) {
    private val dateFormat = SimpleDateFormat("MM.dd.", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    private var binding = PhotoListItemBinding.bind(view)

    fun bind(photo: Photo) {
        binding.imageView.setImageBitmap(photo.bitmap)
        binding.tvFoodName.text = photo.name
        binding.tvDate.text = dateFormat.format(photo.dateTaken)
        binding.tvTime.text = timeFormat.format(photo.dateTaken)
        view.setOnLongClickListener { longTapCallback(photo) }
    }
}