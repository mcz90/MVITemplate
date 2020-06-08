package com.czyzewski.mvi.staterecorder

import kotlinx.serialization.ImplicitReflectionSerializer

@ImplicitReflectionSerializer
data class StateRecorderConfig(
    var enabled: Boolean = false,
    var maxRecords: Int = -1,
    var testButtonEnabled: Boolean = true,
    var isInReplayMode: Boolean = false
)

@ImplicitReflectionSerializer
object StateRecorderSetup {

    private lateinit var config: StateRecorderConfig

    fun init(setup: StateRecorderConfig.() -> Unit) {
        config = StateRecorderConfig().apply(setup)
    }

    fun getConfig() = config
}
