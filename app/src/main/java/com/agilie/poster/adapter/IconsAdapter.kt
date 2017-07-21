package com.agilie.poster.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.agilie.poster.R
import com.bumptech.glide.Glide


class IconsAdapter(val context: Context, var filters: List<Int>) : RecyclerView.Adapter<IconsAdapter.ViewHolder>() {

	interface OnItemClickListener {
		fun onItemClick(position: Int)
	}

	var itemListener: OnItemClickListener? = null

	override fun getItemCount(): Int {
		return filters.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.itemPosition = position
		holder.bindHolder(filters[position])
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context)
				.inflate(R.layout.filter_item, parent, false)
		return ViewHolder(itemView)
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var itemPosition = 0

		init {
			itemView.setOnClickListener { itemListener?.onItemClick(itemPosition) }
		}

		fun bindHolder(resId: Int) {
			Glide.with(context)
					.load("")
					.placeholder(resId)
					.into(itemView as ImageView)
		}
	}
}