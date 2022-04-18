package com.virtual.box.core.service

import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L

/**
 * 守护进程服务
 * 用于进程保活
 */
class DaemonService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        L.sd("守护进程 >> onCreate")
        initNotificationManager()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        L.sd("守护进程 >> onStartCommand")
        val innerIntent = Intent(this, DaemonInnerService::class.java)
        startService(innerIntent)
        if (BuildCompat.isAtLeastOreo) {
            showNotification()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        L.sd("守护进程 >> onDestroy")
        super.onDestroy()
    }

    private fun showNotification() {
        L.sd("守护进程 >> 添加通知，设置成前台服务")
        val builder = NotificationCompat.Builder(applicationContext, "$packageName.blackbox")
            .setPriority(NotificationCompat.PRIORITY_MAX)
        startForeground(NOTIFY_ID, builder.build())
    }

    @SuppressLint("NewApi")
    private fun initNotificationManager() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val CHANNEL_ONE_ID = "$packageName.blackbox"
        val CHANNEL_ONE_NAME = "blackbox"
        if (BuildCompat.isAtLeastOreo) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            nm.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * 内部进程，主要用来高版本关闭通知
     */
    class DaemonInnerService : Service() {
        override fun onCreate() {
            Log.i(TAG, "DaemonInnerService -> onCreate")
            super.onCreate()
        }

        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            Log.i(TAG, "DaemonInnerService -> onStartCommand")
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFY_ID)
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }

        override fun onDestroy() {
            Log.i(TAG, "DaemonInnerService -> onDestroy")
            super.onDestroy()
        }
    }

    companion object {
        const val TAG = "DaemonService"
        private val NOTIFY_ID = (System.currentTimeMillis() / 1000).toInt()

        @JvmStatic
        fun startService(context: Context){
            val intent = Intent(context , DaemonService::class.java)
            if (BuildCompat.isAtLeastOreo){
                context.startForegroundService(intent)
            }else{
                context.startService(intent)
            }
        }
    }
}