package com.czyzewski.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PROTECTED
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

abstract class MviFragment<ViewModelType : ViewModel>(
    @LayoutRes contentLayoutId: Int,
    viewModelClass: KClass<ViewModelType>
) : Fragment(contentLayoutId), CoroutineScope {

    abstract val view: IMyMviView<out MviState>

    @VisibleForTesting(otherwise = PROTECTED)
    val viewModel: ViewModel by viewModel(viewModelClass) {
        parametersOf(this@MviFragment, viewModelClass)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        job = Job()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }
}
