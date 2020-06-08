package com.czyzewski.mvi

import com.czyzewski.mvi.statereplayview.ScreenStateModel
import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
interface ScreenState {
    fun serialize(): ScreenStateModel
    fun stringify(): String
    fun deserialize(data: String): ScreenState
}
