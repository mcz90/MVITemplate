package com.czyzewski.mvi.staterecorder

import android.content.Context
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.czyzewski.mvi.MviFragment
import com.czyzewski.mvi.ScreenState
import com.czyzewski.mvi.statereplayview.ScreenStateModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlin.reflect.KClass

interface IRecorder : LifecycleObserver


@ObsoleteCoroutinesApi
@FlowPreview
@ExperimentalCoroutinesApi
@ImplicitReflectionSerializer
class StateRecorder(
    private val stateStorage: StateStorage,
    private val states: List<KClass<out ScreenState>>
) : IRecorder {

    init {
        if (states.distinct().size != states.size) {
            val duplicates = states.groupBy { it.simpleName }
                .filter { it.value.size == 1 }
                .flatMap { it.value }
                .joinToString(separator = ", ")
            throw IllegalArgumentException("states of StateRecorder has duplicates: $duplicates")
        }
    }

    var currentStateClass: KClass<out ScreenState>? = null
    private var replayModeStateListener: ReplayModeStateListener? = null

    fun setReplayModeStateChangedListener(listener: ReplayModeStateListener) {
        replayModeStateListener = listener
    }

    fun removeReplayModeStateChangedListener() {
        replayModeStateListener = null
    }

    fun save(data: ScreenStateModel, rule: Boolean = true) {
        stateStorage
            .takeIf { getConfig().enabled && rule && getConfig().isInReplayMode }
            ?.add(data)
    }

    fun saveLast(data: ScreenStateModel, rule: Boolean = true) {
        stateStorage
            .takeIf { getConfig().enabled && rule && getConfig().isInReplayMode }
            ?.addLast(data)
    }


    fun getLast(): ScreenStateModel? {
        return currentStateClass?.let {
            stateStorage
                .takeIf { getConfig().enabled && getConfig().isInReplayMode }
                ?.getLast(it)
        }
    }

    fun getAll(): List<ScreenStateModel> {
        return currentStateClass?.let {
            stateStorage
                .takeIf { getConfig().enabled && getConfig().isInReplayMode }
                ?.getAll(it)
        } ?: emptyList()
    }

    fun clear() {
        stateStorage
            .takeIf { getConfig().enabled }
            ?.clear()
    }

    fun setReplayMode(mode: Boolean) {
        getConfig().isInReplayMode = mode
        replayModeStateListener?.onChanged(mode)
    }

    @OnLifecycleEvent(Event.ON_START)
    fun disableReplayMode() {
        replayModeStateListener?.onChanged(false)
    }

    fun updateCurrentState(fragmentClass: KClass<out MviFragment>) {
        val fragmentPrefix = fragmentClass.simpleName?.substringBefore("Fragment")
        currentStateClass =
            states.first { it.simpleName?.substringBefore("State") == fragmentPrefix }
    }


    fun getConfig() = StateRecorderSetup.getConfig()

    @ObsoleteCoroutinesApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    class Builder(
        private val appContext: Context,
        private val states: List<KClass<out ScreenState>>
    ) {

        fun build(): StateRecorder = StateRecorder(
            StateStorage(appContext.getSharedPreferences(STATE_PREFS_KEY, Context.MODE_PRIVATE)),
            states
        )

        private val STATE_PREFS_KEY = "STATE_PREFS_KEY"
    }
}

interface ReplayModeStateListener {
    fun onChanged(mode: Boolean)
}
