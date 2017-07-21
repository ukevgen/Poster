package com.agilie.poster.adapter

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.agilie.poster.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.settings_item.view.*

class PhotoSettingsAdapter(val context: Context,
                           var textList: Array<String>,
                           var imagesArray: TypedArray) : RecyclerView.Adapter<PhotoSettingsAdapter.ViewHolder>() {


	interface OnItemClickListener {
		fun onItemClick(position: Int)
	}

	var itemListener: OnItemClickListener? = null

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.apply {
			itemPosition = position
			bindHolder(textList[position], imagesArray.getResourceId(position, -1))
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context)
				.inflate(R.layout.settings_item, parent, false)
		return ViewHolder(itemView)
	}

	override fun getItemCount(): Int {
		return textList.size
	}

	open inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		var itemPosition = 0
		var settingsImage: ImageView? = null
		var settingsText: TextView? = null

		init {
			settingsImage = view.settings_image
			settingsText = view.settings_text
			view.setOnClickListener { itemListener?.onItemClick(itemPosition) }
		}

		fun bindHolder(t: String, resId: Int) {
			settingsText?.text = t
			Glide.with(context)
					.load("")
					.placeholder(resId)
					.into(settingsImage)
		}
	}

}