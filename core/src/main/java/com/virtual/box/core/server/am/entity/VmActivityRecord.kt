package com.virtual.box.core.server.am.entity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.IBinder

class VmActivityRecord(
    val userId: Int,
    val intent: Intent,

    val activityInfo: ActivityInfo
){
    var component: ComponentName? = null
    var token: IBinder? = null
    var resultTo: IBinder? = null
    var taskRecord: VmTaskRecord? = null
    /**
     * 标记Activity是否finish
     */
    var finished = false
}