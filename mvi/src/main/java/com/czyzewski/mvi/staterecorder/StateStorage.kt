package com.czyzewski.mvi.staterecorder

import android.content.SharedPreferences
import com.czyzewski.mvi.ScreenState
import com.czyzewski.mvi.statereplayview.ScreenStateModel
import kotlin.reflect.KClass
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse

@ImplicitReflectionSerializer
class StateStorage(private val sharedPreferences: SharedPreferences) {

    fun add(model: ScreenStateModel) {
        val key = "$${model.name}\n${System.currentTimeMillis()}"
        val data = Json(JsonConfiguration.Stable).stringify(ScreenStateModel.serializer(), model)
        sharedPreferences.edit().putString(key, data).apply()
    }

    fun addLast(model: ScreenStateModel) {
        val key = "$LAST_STATE_MODEL_KEY${model.name}"
        val data = Json(JsonConfiguration.Stable).stringify(ScreenStateModel.serializer(), model)
        sharedPreferences.edit().putString(key, data).apply()
    }

    fun getAll(stateClass: KClass<out ScreenState>): List<ScreenStateModel> {
        return sharedPreferences.all.entries
            .filter { it.key.contains(stateClass.simpleName.toString()) }
            .sortedByDescending { it.key.split("\n").last().toLong() }
            .map { Json(JsonConfiguration.Stable).parse(it.value.toString()) as ScreenStateModel }
    }

    fun getLast(stateClass: KClass<out ScreenState>): ScreenStateModel? {
        return sharedPreferences.all.entries
            .filter { it.key.contains(stateClass.simpleName.toString()) }
            .takeIf { it.isNotEmpty() }
            ?.map { Json(JsonConfiguration.Stable).parse(it.value.toString()) as ScreenStateModel }
            ?.first()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val LAST_STATE_MODEL_KEY = "LAST_"
    }
}
