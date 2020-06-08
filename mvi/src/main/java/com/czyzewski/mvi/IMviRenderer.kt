package com.czyzewski.mvi

interface IMviRenderer<State, Components> {
    fun render(state: State)
    fun onConfigurationChanged(orientation: Int)
    fun attach(components: Components)
}
