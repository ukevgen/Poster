package com.agilie.poster.view.fragments.fill

import com.agilie.poster.view.fragments.FragmentContract
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface FillView : MvpView, FragmentContract.View