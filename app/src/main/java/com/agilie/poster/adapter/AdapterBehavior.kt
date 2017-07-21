package com.agilie.poster.adapter

import android.support.v7.widget.RecyclerView

interface AdapterBehavior {
	fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>?

	interface OnIconsClickListener {
		fun onItemClick(position: Int)
	}

	interface OnItemClickListener {
		fun onItemClick(path: String?)
	}
}