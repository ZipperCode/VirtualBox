package com.virtual.box.core.manager

import android.app.ActivityManager
import android.content.Context
import android.util.ArrayMap
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.server.am.entity.VmTaskRecord
import java.util.LinkedHashMap

object VmActivityStackManager {
    /**
     * 系统的ams服务
     */
    private val activityManager: ActivityManager = VirtualBox.get().hostContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    /**
     * 记录的用户任务栈
     * key: 任务id
     * value: 任务栈
     */
    private val taskRecordMap: MutableMap<Int, VmTaskRecord> = ArrayMap()

    /**
     * 启动一个顶层的Activity
     */
    fun launchExistsTask(packageName: String, userId: Int){
        for (taskRecord in taskRecordMap.values) {
            if (taskRecord.taskAffinity == packageName && taskRecord.userId == userId){
                activityManager.moveTaskToFront(taskRecord.taskId, 0)
                return
            }
        }
    }

    fun findTopTaskProcessName(){
        for (value in taskRecordMap.values) {

        }
    }

    /**
     * 指定的任务栈是否存在
     */
    fun findTaskRecordByTaskAffinityLocked(userId: Int, taskAffinity: String): VmTaskRecord? {
        synchronized(taskRecordMap) {
            for (next in taskRecordMap.values) {
                if (userId == next.userId && next.taskAffinity == taskAffinity) {
                    return next
                }
            }
            return null
        }
    }

//    fun createNewTaskRecord(): VmTaskRecord{
//
//    }
}