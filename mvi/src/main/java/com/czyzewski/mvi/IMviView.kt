package com.czyzewski.mvi

import androidx.lifecycle.LifecycleObserver

interface MviState

interface MviIntent

interface IMyMviView<State> : LifecycleObserver {
    fun render(state: State)
    fun onConfigurationChanged(orientation: Int)
}