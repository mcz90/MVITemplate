package com.czyzewski.mvitemplate

import android.app.Application
import com.facebook.stetho.Stetho
import kotlinx.serialization.UnstableDefault
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MviTemplateApplication : Application() {

    @UnstableDefault
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        Timber.plant(Timber.DebugTree())
        startKoin { androidContext(this@MviTemplateApplication) }
    }
}
