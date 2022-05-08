package com.virtual.box.core.server.am.entity

import android.content.Intent
import java.util.*

/**
 * 虚拟进程的任务记录
 *
 */
class VmTaskRecord(
    /**
     * 窗口栈id
     */
    @JvmField
    val taskId: Int,
    /**
     * 用户id
     */
    @JvmField
    val userId: Int,
    /**
     * 窗口栈名称
     */
    @JvmField
    val taskAffinity: String,
    /**
     * 源intent 第一个启动的Intent
     */
    @JvmField
    var rootIntent: Intent
) {
    /**
     * 窗口栈的Activity记录
     */
    val activityTaskList: MutableList<VmActivityRecord> = LinkedList()
}