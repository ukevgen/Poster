package com.agilie.poster.view.fragments.filter

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R
import com.agilie.poster.adapter.AdapterBehavior
import com.agilie.poster.adapter.PhotoIconsAdapter
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_fill.*
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : BaseFragment(),
		FilterView,
		AdapterBehavior.OnItemClickListener {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_filter, container, false)

		return view
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val adapter = getAdapter<RecyclerView.ViewHolder>()
		filter_recycler.apply {
			this.adapter = adapter
			layoutManager = LinearLayoutManager(this@FilterFragment.context, LinearLayoutManager.HORIZONTAL, false)
		}
	}

	override fun onItemClick(position: Int) {

	}

	override fun initView() {

	}

	override fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>? {
		val filters = context.resources.obtainTypedArray(R.array.filterImages)
		val filtersList = (0..filters.length() - 1).map { filters.getResourceId(it, -1) }
		val adapter = PhotoIconsAdapter(this.context, filtersList).apply { itemListener = this@FilterFragment }
		return adapter as RecyclerView.Adapter<VH>
	}

	override fun onAnimationSettings(show: Boolean) {
		when (show) {
			true -> {

			}
			false -> {

			}
		}
	}

	override fun showRecycler() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun hideRecycler() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun showSeekBar() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun hideSeekBar() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onAnimationAllNavigation() {
		// Empty
	}
}