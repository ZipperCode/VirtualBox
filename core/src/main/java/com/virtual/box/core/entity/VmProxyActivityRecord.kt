package com.virtual.box.core.entity

import android.content.Intent
import android.content.pm.ActivityInfo
import com.virtual.box.core.helper.IntentHelper

class VmProxyActivityRecord(
    val userId: Int,
    val originIntent: Intent,
    val activityInfo: ActivityInfo,
)