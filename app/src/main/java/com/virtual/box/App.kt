package com.virtual.box

import android.app.Application
import android.content.Context
import com.virtual.box.core.VirtualBox

class App: Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        VirtualBox.get().doAttachAppBaseContext(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}