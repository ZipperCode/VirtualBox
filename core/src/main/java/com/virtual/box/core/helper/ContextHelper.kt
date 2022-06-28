package com.virtual.box.core.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.virtual.box.base.util.log.L
import com.virtual.box.core.manager.AppActivityThread
import com.virtual.box.reflect.android.app.HContextImpl
import com.virtual.box.reflect.android.content.HContentResolver
import java.lang.Exception

object ContextHelper {

    const val MAX_DEEP = 10
    /**
     * 修复上下文中的包名
     */
    fun fixPackageName(context: Context, packageName: String){
        try {
            var preContext: Context? = context
            var curContext: Context? = context
            var deep = 0
            do {
                if (curContext is ContextWrapper){
                    preContext = curContext
                    curContext = curContext.baseContext
                }
            }while (curContext != null && preContext != curContext && deep++ < MAX_DEEP)
            HContextImpl.mBasePackageName.set(curContext, packageName)
            HContextImpl.mOpPackageName.set(curContext, packageName)

            val contentResolver = HContextImpl.mContentResolver.get(curContext)
            if (contentResolver != null){
                HContentResolver.mPackageName.set(contentResolver, packageName)
            }
        }catch (e: Exception){
            L.printStackTrace(e)
        }
    }

    fun fixBaseContextLoadApk(activity: Activity){
        var preContext: Context? = activity
        var curContext: Context? = activity
        var deep = 0
        do {
            if (curContext is ContextWrapper){
                preContext = curContext
                curContext = curContext.baseContext
            }
        }while (curContext != null && preContext != curContext && deep++ < MAX_DEEP)

        if (curContext !is ContextWrapper){
            HContextImpl.mPackageInfo.set(curContext, AppActivityThread.mVmLoadedApk)
        }
    }

}