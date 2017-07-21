package com.agilie.poster.view.fragments.fill

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R
import com.agilie.poster.adapter.AdapterBehavior
import com.agilie.poster.adapter.PhotoSettingsAdapter
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_fill.*

class FillFragment : BaseFragment(), FillView, AdapterBehavior.OnIconsClickListener {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_fill, container, false)

		return view
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val adapter = getAdapter<RecyclerView.ViewHolder>()

		fill_recycler.apply {
			this.adapter = adapter
			layoutManager = LinearLayoutManager(this@FillFragment.context, LinearLayoutManager.HORIZONTAL, false)
			scrollToPosition(1)
		}
	}

	override fun onItemClick(position: Int) {
		// Empty
	}

	override fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>? {
		val settingsImage = context.resources.obtainTypedArray(R.array.settingImages)
		val settingsText = context.resources.getStringArray(R.array.settingsText)
		val adapter = PhotoSettingsAdapter(context, settingsText, settingsImage).apply {
			itemListener = this@FillFragment
		}
		return adapter as RecyclerView.Adapter<VH>
	}
}