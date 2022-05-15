package com.virtual.box.core.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.server.IVmServerConnect

class BindDaemonService: Service() {

    private val logger = Logger.getLogger(L.SERVER_TAG,"守护进程（bind）")

    override fun onCreate() {
        super.onCreate()
        logger.d("创建服务")
        initNotificationManager()
    }

    override fun onBind(intent: Intent?): IBinder {
        if (BuildCompat.isAtLeastOreo) {
            showNotification()
        }
        return VmServiceConnect()
    }

    private fun showNotification() {
        logger.d("添加通知，设置成前台服务")
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_MAX)
        startForeground(NOTIFY_ID, builder.build())
    }

    class VmServiceConnect: IVmServerConnect.Stub(){
        override fun checkServer(): Boolean {
            return true
        }
    }

    private fun initNotificationManager() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val CHANNEL_ONE_ID = CHANNEL_ID
        val CHANNEL_ONE_NAME = "virtual_box"
        if (BuildCompat.isAtLeastOreo) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            nm.createNotificationChannel(notificationChannel)
        }
    }

    companion object{

        private const val NOTIFY_ID = 0x1111

        private const val CHANNEL_ID = "com.virtual.box.virtual_box"


        fun bindService(context: Context, connection: ServiceConnection) {
            val intent = Intent(context, BindDaemonService::class.java)
            context.bindService(
                intent, connection, BIND_AUTO_CREATE
                        or BIND_ADJUST_WITH_ACTIVITY
                        or BIND_IMPORTANT
                        or BIND_WAIVE_PRIORITY
            )
        }
    }
}