package com.czyzewski.mvi

import androidx.lifecycle.*
import com.czyzewski.mvi.staterecorder.StateRecorder
import kotlinx.coroutines.*
import kotlinx.serialization.ImplicitReflectionSerializer
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

@FlowPreview
@ObsoleteCoroutinesApi
@ImplicitReflectionSerializer
@ExperimentalCoroutinesApi
abstract class MviViewModel<State : ScreenState, Intent : ScreenIntent>(
    private val lifecycleOwner: LifecycleOwner
) : ViewModel(), CoroutineScope, KoinComponent, LifecycleObserver {

    private val stateRecorder: StateRecorder by inject()

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    protected val state = MutableLiveData<State>()

    private lateinit var renderBlock: (State) -> Unit

    abstract fun onIntentReceived(intent: Intent)

    fun getStateLiveData() = state

    override fun onCleared() {
        state.removeObservers(lifecycleOwner)
        super.onCleared()
    }

    private fun observe() {
        state.removeObservers(lifecycleOwner)
        state.observe(lifecycleOwner, Observer { state ->
            stateRecorder.saveLast(state.serialize())
            renderBlock(state)
        })
    }

    fun attach(renderBlock: (ScreenState) -> Unit) {
        this.renderBlock = renderBlock
        observe()
    }
}
