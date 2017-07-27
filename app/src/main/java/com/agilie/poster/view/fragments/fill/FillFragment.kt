package com.agilie.poster.view.fragments.fill

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.Constants
import com.agilie.poster.R
import com.agilie.poster.adapter.AdapterBehavior
import com.agilie.poster.adapter.PhotoSettingsAdapter
import com.agilie.poster.presenter.fill.FillPresenterImpl
import com.agilie.poster.utils.dpToPx
import com.agilie.poster.view.activity.MainActivity
import com.agilie.poster.view.activity.MainView
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fill.*


class FillFragment : BaseFragment(), FillView, AdapterBehavior.OnItemClickListener {

	private val TAG = "FillFragment"
	private var itemSelected = false
	private var visibleRecycler = false
	lateinit var fillPresenter: FillPresenterImpl
	private val resetConstraintSet = ConstraintSet()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_fill, container, false)

		return view
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		initView()

		fillPresenter = FillPresenterImpl(this)
	}

	override fun onResume() {
		super.onResume()
		fillPresenter.resume()
	}

	override fun onPause() {
		//fillPresenter.pause()
		visibleRecycler = false
		onAnimationSettings()
		super.onPause()
	}

	override fun initView() {
		val adapter = getAdapter<RecyclerView.ViewHolder>()
		fill_recycler.apply {
			this.adapter = adapter
			layoutManager = LinearLayoutManager(this@FillFragment.context, LinearLayoutManager.HORIZONTAL, false)
			scrollToPosition(1)
		}
		fill_progress_ok.setOnClickListener {
			fillPresenter.onProgressOk()

		}
		fill_progress_cancel.setOnClickListener {
			fillPresenter.onProgressCancel()
		}

		resetConstraintSet.clone(fill_constraintLayout)
	}

	override fun onItemClick(position: Int) {
		onAnimationSettings()
	}

	override fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>? {
		val settingsImage = context.resources.obtainTypedArray(R.array.settingImages)
		val settingsText = context.resources.getStringArray(R.array.settingsText)
		val adapter = PhotoSettingsAdapter(context, settingsText, settingsImage).apply {
			itemListener = this@FillFragment
		}
		return adapter as RecyclerView.Adapter<VH>
	}

	override fun onAnimationSettings() {
		if (activity !is MainView) {
			return
		}
		val tabLayout = activity.tab_layout
		val toolBar = activity.toolbar_main

		when (itemSelected) {
			true -> {
				showRecycler()
				// Show toolbar and setting container
				animateViewElement(view = toolBar, toYPosition = 0f, duration = Constants.DURATION)
				animateViewElement(tabLayout, 0f, Constants.DURATION)
				// Hide progress container
				hideSeekBar()
				itemSelected = false
			}
			false -> {
				hideRecycler()
				animateViewElement(view = toolBar, toYPosition = (-toolBar.bottom).toFloat(), duration = Constants.DURATION)
				animateViewElement(tabLayout, tabLayout.bottom.toFloat(), Constants.DURATION)
				// Show progress container
				showSeekBar()
				itemSelected = true
			}
		}
	}

	override fun showSeekBar() {
		val layout = fill_constraintLayout
		TransitionManager.beginDelayedTransition(fill_constraintLayout)
		val margin = dpToPx(context, R.dimen.progress_margin_bottom)

		val set = ConstraintSet()
		set.clone(layout)
		set.clear(R.id.fill_progress_container, ConstraintSet.TOP)
		set.connect(R.id.fill_progress_container, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
		set.setMargin(R.id.fill_progress_container, ConstraintSet.BOTTOM, margin)
		set.applyTo(layout)
	}

	override fun hideSeekBar() {
		val layout = fill_constraintLayout
		resetConstraintSet.applyTo(layout)
	}

	override fun showRecycler() {

		val layout = fill_constraintLayout
		TransitionManager.beginDelayedTransition(fill_constraintLayout)
		val set = ConstraintSet()
		set.clone(layout)
		set.clear(R.id.fill_recycler_container, ConstraintSet.TOP)
		set.connect(R.id.fill_recycler_container, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
		set.applyTo(layout)
	}

	override fun hideRecycler() {
		val layout = fill_constraintLayout ?: return
		TransitionManager.beginDelayedTransition(fill_constraintLayout)
		val set = ConstraintSet()
		set.clone(layout)
		set.clear(R.id.fill_recycler_container, ConstraintSet.BOTTOM)
		set.connect(R.id.fill_recycler_container, ConstraintSet.TOP, R.id.fill_progress_container, ConstraintSet.BOTTOM)
		set.applyTo(layout)
	}

	override fun onAnimationFragment(selected: MainActivity.TabSelectedStatus) {
		when (selected) {
			MainActivity.TabSelectedStatus.SELECTED -> {
				// Empty
			}
			MainActivity.TabSelectedStatus.UNSELECTED -> {
				// Hide recycler
				hideRecycler()
				visibleRecycler = false
			}
			MainActivity.TabSelectedStatus.RESELECTED -> {
				// Hide or show recycler
				setRecyclerVisibleStatus()
			}
		}
	}

	private fun setRecyclerVisibleStatus() {
		when (visibleRecycler) {
			true -> {
				hideRecycler()
				visibleRecycler = false
			}
			false -> {
				showRecycler()
				visibleRecycler = true
			}
		}
	}
}