package com.czyzewski.mvi

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PROTECTED
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.czyzewski.mvi.staterecorder.StateRecorder
import com.czyzewski.mvi.statereplayview.ScreenStateModel
import com.czyzewski.mvi.statereplayview.StateReplayView
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.ImplicitReflectionSerializer
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.full.createInstance

@ExperimentalCoroutinesApi
@FlowPreview
@ObsoleteCoroutinesApi
@ImplicitReflectionSerializer
abstract class MviFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId),
    CoroutineScope {

    private val stateRecorder: StateRecorder by inject {
        parametersOf(this@MviFragment::class)
    }

    var components: ViewComponents? = null

    abstract val view: IMviView<out ScreenState, out ViewComponents>

    @VisibleForTesting(otherwise = PROTECTED)
    abstract val viewModel: MviViewModel<out ScreenState, out ScreenIntent>

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    abstract fun prepareMocks(): List<ScreenStateModel>

    override fun onStart() {
        super.onStart()
        stateRecorder.updateCurrentState(this::class)
        viewModel.attach { state -> view.render(state) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        job = Job()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        view.onConfigurationChanged(newConfig.orientation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.wtf("wtf", "onViewCreated ${this::class.simpleName}")
        lifecycle.addObserver(viewModel)
        lifecycle.addObserver(this.view)
        stateRecorder.getConfig()
            .takeIf { it.enabled }
            ?.let {
                lifecycle.addObserver(stateRecorder)
                attachRecordButton()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        components = null
        view.detach()
        lifecycle.removeObserver(stateRecorder)
        lifecycle.removeObserver(viewModel)
        lifecycle.removeObserver(view)
        Log.wtf("wtf", "onDestroyView ${this::class.simpleName}")
    }

    private fun Fragment.attachRecordButton() {
        val stateReplayView = StateReplayView
            .createInstance(this@MviFragment.requireContext())
            .apply { addMocks(prepareMocks()) }
        this.view?.rootView
            ?.findViewById<ViewGroup>(android.R.id.content)
            ?.apply {
                removeStateReplayViews()
                addView(stateReplayView)
            }
        stateReplayView.apply {
            floatingActionButtonClickListener {
                stateRecorder.setReplayMode(true)
            }
            launch {
                stateModelClicked()
                    .consumeEach { screenState ->
                        stateRecorder.currentStateClass
                            ?.let {
                                this@MviFragment.view.render(
                                    it.createInstance().deserialize(screenState.data)
                                )
                            }
                    }
            }
        }
    }

    private fun ViewGroup.removeStateReplayViews() {
        children.filterIsInstance<StateReplayView>()
            .let { stateReplayViews ->
                stateReplayViews.forEach { this.removeView(it) }
            }
    }
}
