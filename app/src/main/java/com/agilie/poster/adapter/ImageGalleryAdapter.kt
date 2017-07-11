package com.agilie.poster.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R


class ImageGalleryAdapter(val context: Context, val photos: List<Int>) : RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {


	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
			ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.gallery_item, parent, false))

	override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
		holder?.bindData(photos[position])
	}

	override fun getItemCount() = photos.size

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		fun bindData(item: Int) {

		}
	}
}