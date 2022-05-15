package com.virtual.box.core.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.core.content.ContextCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.manager.VmActivityThread
import com.virtual.box.reflect.android.app.HContextImpl
import java.lang.Exception

object ContextHelper {

    /**
     * 修复上下文中的包名
     */
    fun fixPackageName(context: Context, packageName: String){
        try {
            var preContext: Context? = context
            var curContext: Context? = context
            do {
                if (curContext is ContextWrapper){
                    preContext = curContext
                    curContext = curContext.baseContext
                }
            }while (curContext != null && preContext != curContext)
            HContextImpl.mBasePackageName.set(packageName)
            HContextImpl.mOpPackageName.set(packageName)

        }catch (e: Exception){
            L.printStackTrace(e)
        }
    }

    fun fixBaseContextLoadApk(activity: Activity){
        var preContext: Context? = activity
        var curContext: Context? = activity
        do {
            if (curContext is ContextWrapper){
                preContext = curContext
                curContext = curContext.baseContext
            }
        }while (curContext != null && preContext != curContext)

        if (curContext !is ContextWrapper){
            HContextImpl.mPackageInfo.set(curContext, VmActivityThread.mVmLoadedApk)
        }
    }

}