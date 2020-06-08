package com.czyzewski.mvi.statereplayview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.czyzewski.mvi.R
import com.czyzewski.mvi.statereplayview.StateRecordsAdapter.ScreenViewHolder
import kotlinx.android.synthetic.main.item_screen_state.view.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@ImplicitReflectionSerializer
@Serializable
data class ScreenStateModel(
    val name: String,
    val data: String,
    val timestamp: Long
)

@ImplicitReflectionSerializer
class StateRecordsAdapter(private val states: MutableList<ScreenStateModel> = mutableListOf()) :
    Adapter<ScreenViewHolder>() {

    private val mockedStates = mutableListOf<ScreenStateModel>()

    private var onCardClickListener: ((ScreenStateModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScreenViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_screen_state, parent, false)
        )

    override fun getItemCount(): Int {
        return states.size
    }

    override fun onBindViewHolder(holder: ScreenViewHolder, position: Int) {
        val state = states[position]
        holder.bind(state)
        holder.setOnClickListener { onCardClickListener?.invoke(it) }
    }

    fun addStates(list: List<ScreenStateModel>) {
        states.clear()
        states.addAll(mockedStates)
        states.addAll(list)
        notifyDataSetChanged()
    }

    fun addMocks(mocks: List<ScreenStateModel>) {
        mockedStates.clear()
        mockedStates.addAll(mocks)
    }

    fun setOnCardClickListener(onCardClickListener: (ScreenStateModel) -> Unit) {
        this.onCardClickListener = onCardClickListener
    }

    inner class ScreenViewHolder(private val view: View) : ViewHolder(view) {

        private val formatter = SimpleDateFormat("HH:mm:ss:SSS dd.MM.yyyy", Locale.getDefault())

        private var onClickListener: ((ScreenStateModel) -> Unit)? = null

        fun bind(state: ScreenStateModel): Unit =
            with(view) {
                fragmentName.text = "${state.name}\n${formatter.format(Date(state.timestamp))}"
                cardView.setOnClickListener { onClickListener?.invoke(state) }
            }

        fun setOnClickListener(onClickListener: (ScreenStateModel) -> Unit) {
            this.onClickListener = onClickListener
        }

        fun detach() {
            onClickListener = null
        }
    }
}
