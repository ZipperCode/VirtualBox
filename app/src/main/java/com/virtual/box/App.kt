package com.virtual.box

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.virtual.box.core.VirtualBox

class App: Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        VirtualBox.get().doAttachAppBaseContext(this)
    }

    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
            .detectAll()//监测所有内容
            .penaltyLog()//违规对log日志
//            .penaltyDeath()//违规Crash
            .build());
    }
}