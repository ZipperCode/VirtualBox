package com.virtual.box.core.hook.delegate

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.IBinder
import android.os.UserHandle
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.compat.ComponentFixCompat
import com.virtual.box.core.hook.IInjectHook
import com.virtual.box.core.manager.HookManager
import com.virtual.box.core.manager.VmActivityManager
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.HLoadedApk
import java.lang.ref.WeakReference

class AppInstrumentation : BaseInstrumentationDelegate(), IInjectHook {

    val logger = Logger.Companion.getLogger(L.HOOK_TAG,"AppInstrumentation")

    override fun initHook() {
        try {
            val mainAThread = VirtualBox.get().mainAThread
            val mInstrumentation = HActivityThread.mInstrumentation.get(mainAThread)
            if (mInstrumentation === this || checkInstrumentation(mInstrumentation)) return
            mBaseInstrumentation = mInstrumentation
            HActivityThread.mInstrumentation[mainAThread] = this
        } catch (e: Exception) {
            logger.e(e)
        }
    }

    override fun isHooked(): Boolean {
        return checkInstrumentation(HActivityThread.mInstrumentation.get(VirtualBox.get().mainAThread))
    }

    private fun checkInstrumentation(instrumentation: Instrumentation): Boolean {
        if (instrumentation is AppInstrumentation) {
            return true
        }
        var clazz: Class<*>? = instrumentation.javaClass
        if (Instrumentation::class.java == clazz) {
            return false
        }
        do {
            assert(clazz != null)
            val fields = clazz!!.declaredFields
            for (field in fields) {
                if (Instrumentation::class.java.isAssignableFrom(field.type)) {
                    field.isAccessible = true
                    try {
                        val obj = field[instrumentation]
                        if (obj is AppInstrumentation) {
                            return true
                        }
                    } catch (e: Exception) {
                        return false
                    }
                }
            }
            clazz = clazz.superclass
        } while (Instrumentation::class.java != clazz)
        return false
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        super.finish(resultCode, results)
    }

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
//        ContextCompat.fix(context)
        return super.newApplication(cl, className, context)
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
//        Debug.waitForDebugger()
        if (BuildCompat.isAtLeastS){
            ComponentFixCompat.fixActivityWithOnCreate(activity)
        }
        super.callActivityOnCreate(activity, icicle)
    }

    override fun callApplicationOnCreate(app: Application) {
        L.hdParamTag(TAG, "app = %s",app.javaClass.name)
        super.callApplicationOnCreate(app)
    }

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newActivity(cl: ClassLoader, className: String, intent: Intent): Activity {
        // Debug.waitForDebugger()
        return try {
            super.newActivity(cl, className, intent)
        } catch (e: ClassNotFoundException) {
            // 替换类加载器进行加载
            val component = intent.component
            val packageName = component!!.packageName
            var targetClassLoader: ClassLoader? = null
            val resources = HActivityThread.mPackages[ActivityThread.currentActivityThread()]
            if (resources != null) {
                val loadApkRef = resources[packageName] as WeakReference<*>?
                if (loadApkRef?.get() != null) {
                    targetClassLoader = HLoadedApk.mClassLoader[loadApkRef.get()]
                }
            }
            return mBaseInstrumentation.newActivity(targetClassLoader ?: cl, className, intent)
        }
    }

    override fun startActivitySync(intent: Intent): Activity {
        return super.startActivitySync(intent)
    }

    override fun callActivityOnNewIntent(activity: Activity?, intent: Intent?) {
        super.callActivityOnNewIntent(activity, intent)
    }

    @Throws(Throwable::class)
    override fun execStartActivity(
        context: Context,
        contextThread: IBinder,
        token: IBinder,
        activity: Activity,
        intent: Intent,
        requestCode: Int
    ): ActivityResult {
        L.hdParamTag(TAG, "context = %s, contextThread = %s, token = %s, activity = %s, intent = %s, requestCode = %s",
            context, contextThread, token, activity, intent, requestCode)
        var realIntent: Intent? = intent
        if (VirtualBox.get().isVirtualProcess) {
            val shadowIntent = VmActivityManager.prepareStartActivity(intent, 0)
            if (shadowIntent != null){
                realIntent = shadowIntent
            }
        }
        return super.execStartActivity(context, contextThread, token, activity, realIntent, requestCode)
    }

    @Throws(Throwable::class)
    override fun execStartActivity(
        context: Context,
        contextThread: IBinder?,
        token: IBinder?,
        fragment: Fragment?,
        intent: Intent,
        requestCode: Int
    ): ActivityResult {
        L.hdParamTag(TAG, "context = %s, contextThread = %s, token = %s, fragment = %s, intent = %s, requestCode = %s", context, contextThread, token, fragment, intent, requestCode)
        var realIntent: Intent? = intent
        if (VirtualBox.get().isVirtualProcess) {
            val shadowIntent = VmActivityManager.prepareStartActivity(intent, 0)
            if (shadowIntent != null){
                realIntent = shadowIntent
            }
        }
        return super.execStartActivity(context, contextThread, token, fragment, realIntent, requestCode)
    }

    @Throws(Throwable::class)
    override fun execStartActivity(
        context: Context,
        contextThread: IBinder?,
        token: IBinder?,
        str: String?,
        intent: Intent,
        requestCode: Int,
        bundle: Bundle?
    ): ActivityResult {
        L.hdParamTag(
            TAG, "context = %s, contextThread = %s, token = %s, str = %s, intent = %s, requestCode = %s, bundle = %s",
            context, contextThread, token, str, intent, requestCode, bundle)
        var realIntent: Intent? = intent
        if (VirtualBox.get().isVirtualProcess) {
            val shadowIntent = VmActivityManager.prepareStartActivity(intent, 0)
            if (shadowIntent != null){
                realIntent = shadowIntent
            }
        }
        return super.execStartActivity(context, contextThread, token, str, realIntent, requestCode, bundle)
    }

    @Throws(Throwable::class)
    override fun execStartActivity(
        context: Context,
        contextThread: IBinder?,
        token: IBinder?,
        activity: Activity?,
        intent: Intent,
        requestCode: Int,
        bundle: Bundle?
    ): ActivityResult {
        // 正常的启动走这边
        L.hdParamTag(
            TAG, "context = %s, contextThread = %s, token = %s, activity = %s, intent = %s, requestCode = %s, bundle = %s",
            context, contextThread, token, activity, intent, requestCode, bundle)
//                Debug.waitForDebugger();
        if (VirtualBox.get().isVirtualProcess) {
//            BActivityManager.get().startActivity(intent, BUserHandle.myUserId())
            return ActivityResult(requestCode, Intent())
        }
        return super.execStartActivity(context, contextThread, token, activity, intent, requestCode, bundle)
    }

    @Throws(Throwable::class)
    override fun execStartActivity(
        context: Context,
        contextThread: IBinder?,
        token: IBinder?,
        fragment: Fragment?,
        intent: Intent,
        requestCode: Int,
        bundle: Bundle?
    ): ActivityResult {
        L.hdParamTag(
            TAG, "context = %s, contextThread = %s, token = %s, fragment = %s, intent = %s, requestCode = %s, bundle = %s",
            context, contextThread, token, fragment, intent, requestCode, bundle)
        var realIntent: Intent? = intent
        if (VirtualBox.get().isVirtualProcess) {
            val shadowIntent = VmActivityManager.prepareStartActivity(intent, 0)
            if (shadowIntent != null){
                realIntent = shadowIntent
            }
        }
        return super.execStartActivity(context, contextThread, token, fragment, realIntent, requestCode, bundle)
    }

    @Throws(Throwable::class)
    override fun execStartActivity(
        context: Context,
        iBinder: IBinder?,
        iBinder2: IBinder?,
        activity: Activity?,
        intent: Intent,
        requestCode: Int,
        bundle: Bundle?,
        userHandle: UserHandle?
    ): ActivityResult {
        L.hdParamTag(
            TAG,
            "context = %s, iBinder = %s, iBinder2 = %s, activity = %s, intent = %s, requestCode = %s, bundle = %s, userHandle = %s",
            context, iBinder, iBinder2, activity, intent, requestCode, bundle, userHandle
        )
        var realIntent: Intent? = intent
        if (VirtualBox.get().isVirtualProcess) {
            val shadowIntent = VmActivityManager.prepareStartActivity(intent, 0)
            if (shadowIntent != null){
                realIntent = shadowIntent
            }
        }
        return super.execStartActivity(context, iBinder, iBinder2, activity, realIntent, requestCode, bundle, userHandle)
    }

    companion object {
        private val TAG = AppInstrumentation::class.java.simpleName
        private var sAppInstrumentation: AppInstrumentation? = null
        @JvmStatic
        fun get(): AppInstrumentation {
            if (sAppInstrumentation == null) {
                synchronized(AppInstrumentation::class.java) {
                    if (sAppInstrumentation == null) {
                        sAppInstrumentation = AppInstrumentation()
                    }
                }
            }
            return sAppInstrumentation!!
        }
    }
}