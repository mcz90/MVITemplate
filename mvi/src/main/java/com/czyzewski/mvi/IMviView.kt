package com.czyzewski.mvi

import androidx.lifecycle.LifecycleObserver
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
interface IMviView<State : ScreenState, Components : ViewComponents> : LifecycleObserver {
    fun <State> render(state: State)
    fun onConfigurationChanged(orientation: Int)
    fun <Components> attach(components: Components?)
    fun detach()
}
