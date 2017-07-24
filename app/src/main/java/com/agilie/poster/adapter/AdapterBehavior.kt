package com.agilie.poster.adapter

import android.support.v7.widget.RecyclerView

interface AdapterBehavior {
	fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>?

	interface OnItemClickListener {
		fun onItemClick(position: Int)
	}

	interface OnPhotoClickListener {
		fun onPhotoItemClick(path: String?)
	}
}