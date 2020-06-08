package com.czyzewski.mvi.statereplayview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.czyzewski.mvi.R
import com.czyzewski.mvi.staterecorder.StateRecorder
import com.czyzewski.mvi.staterecorder.StateRecorderSetup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.view_state_replay.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import org.koin.core.KoinComponent
import org.koin.core.inject

@ObsoleteCoroutinesApi
@ImplicitReflectionSerializer
@FlowPreview
@ExperimentalCoroutinesApi
class StateReplayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KoinComponent {

    private val stateRecorder: StateRecorder by inject()
    private val adapter = StateRecordsAdapter()
    private val testButtonEnabled = StateRecorderSetup.getConfig().testButtonEnabled
    private var wasMoved = false
    private var onClick: (() -> Unit)? = null

    private val stateModelClickedChannel = BroadcastChannel<ScreenStateModel>(1)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_state_replay, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StateReplayView.adapter
        }
        recordsView.setOnClickListener { hideRecords() }
        isVisible = stateRecorder.getConfig().enabled
        floatingActionButton.apply {
            toggleResource()
            setOnClickListener {
                if (wasMoved || !stateRecorder.getConfig().isInReplayMode) return@setOnClickListener
                onClick?.invoke()
                adapter.apply {
                        setOnCardClickListener {
                            stateModelClickedChannel.sendBlocking(it)
                            hideRecords()
                        }
                        showRecords()
                    }
            }
            setOnLongClickListener {
                if (wasMoved) return@setOnLongClickListener false
                toggleReplayMode()
                true
            }
            setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    ACTION_MOVE -> {
                        wasMoved = true
                        view.apply {
                            y = motionEvent.rawY - height / 2
                            x = motionEvent.rawX - width / 2
                        }
                        return@setOnTouchListener true
                    }
                    ACTION_CANCEL, ACTION_UP -> return@setOnTouchListener wasMoved
                    else -> {
                        wasMoved = false
                        return@setOnTouchListener false
                    }
                }
            }
        }
    }

    private fun toggleReplayMode() {
        stateRecorder.getConfig().isInReplayMode = !stateRecorder.getConfig().isInReplayMode
        floatingActionButton.toggleResource()
    }

    private fun FloatingActionButton.toggleResource() = setImageResource(
        if (stateRecorder.getConfig().isInReplayMode) {
            R.drawable.ic_stop_black_32dp
        } else {
            R.drawable.ic_play_black_32dp
        }
    )

    fun stateModelClicked() = stateModelClickedChannel

    fun floatingActionButtonClickListener(onClick: () -> Unit) {
        this.onClick = onClick
    }

    private fun showRecords() {
        recordsView.isVisible = true && testButtonEnabled
    }

    private fun hideRecords() {
        recordsView.isVisible = false
    }

    fun addMocks(list: List<ScreenStateModel>) {
        adapter.addMocks(list)
    }

    companion object {
        fun createInstance(context: Context) = StateReplayView(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
    }
}
