package com.deep.notify

import android.app.Application
import com.deep.notify.di.AppContainer
import com.deep.notify.di.AppContainerImpl

class NotifyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
