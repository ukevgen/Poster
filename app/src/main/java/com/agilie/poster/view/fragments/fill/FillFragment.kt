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
import com.agilie.poster.view.activity.MainView
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fill.*


class FillFragment : BaseFragment(), FillView, AdapterBehavior.OnItemClickListener {

	private val TAG = "FillFragment"
	private var show = false
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
		fillPresenter.onHideSeekBar()
		fillPresenter.onHideRecycler()
	}

	override fun onPause() {
		show = true
		fillPresenter.pause()
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
		fillPresenter.onItemClick()
	}

	override fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>? {
		val settingsImage = context.resources.obtainTypedArray(R.array.settingImages)
		val settingsText = context.resources.getStringArray(R.array.settingsText)
		val adapter = PhotoSettingsAdapter(context, settingsText, settingsImage).apply {
			itemListener = this@FillFragment
		}
		return adapter as RecyclerView.Adapter<VH>
	}

	override fun onAnimationSettings(show: Boolean) {
		when (show) {
			true -> {
				showRecycler()
			}
			false -> {
				hideRecycler()
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

	override fun onAnimationAllNavigation() {

		if (activity !is MainView) {
			return
		}

		val tabLayout = activity.tab_layout
		val toolBar = activity.toolbar_main
		when (show) {
			true -> {
				showRecycler()
				// Show toolbar and setting container
				animateViewElement(view = toolBar, toYPosition = 0f, duration = Constants.DURATION)
				animateViewElement(tabLayout, 0f, Constants.DURATION)
				// Hide progress container
				hideSeekBar()
				this.show = false
			}
			false -> {
				hideRecycler()
				animateViewElement(view = toolBar, toYPosition = (-toolBar.bottom).toFloat(), duration = Constants.DURATION)
				animateViewElement(tabLayout, tabLayout.bottom.toFloat(), Constants.DURATION)
				// Show progress container
				showSeekBar()

				this.show = true
			}
		}
	}
}