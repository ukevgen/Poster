package com.agilie.poster.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.agilie.poster.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File


class GalleryAdapter(val context: Context, var photos: List<String>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

	var photoListener: AdapterBehavior.OnPhotoClickListener? = null

	override fun getItemCount() = photos.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.path = photos[position]
		holder.bindHolder(photos[position])
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context)
				.inflate(R.layout.gallery_item, parent, false)
		return ViewHolder(itemView)
	}

	inner class ViewHolder : RecyclerView.ViewHolder {

		var path: String? = null

		constructor(itemView: View) : super(itemView) {
			itemView.setOnClickListener { photoListener?.onPhotoItemClick(path) }
		}

		fun bindHolder(path: String) {
			Glide.with(context).load(File(path))
					.crossFade()
					.placeholder(R.drawable.ic_load_image)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into(itemView as ImageView)
		}
	}
}