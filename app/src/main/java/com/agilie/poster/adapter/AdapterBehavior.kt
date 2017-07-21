package com.agilie.poster.adapter

import android.support.v7.widget.RecyclerView

interface AdapterBehavior {
	fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>?
}