package com.virtual.box.core.hook.delegate

import android.annotation.TargetApi
import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.*
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger.Companion.getLogger
import com.virtual.box.core.compat.ComponentFixCompat
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.MirrorReflection.MethodWrapper
import com.virtual.box.reflect.android.app.HActivity
import com.virtual.box.reflect.android.app.HApplication
import com.virtual.box.reflect.android.content.pm.HActivityInfo

abstract class BaseInstrumentationDelegate : Instrumentation() {
    protected var mBaseInstrumentation: Instrumentation? = null

    /**
     * ActivityThread#handleBindApplication中调用
     */
    override fun onCreate(arguments: Bundle?) {
        mBaseInstrumentation?.onCreate(arguments)
    }

    override fun start() {
        mBaseInstrumentation?.start()
    }

    override fun onStart() {
        mBaseInstrumentation?.onStart()
    }

    override fun onException(obj: Any?, e: Throwable?): Boolean {
        logger.e("onException#obj = %s, e = %s", obj, e)
        return mBaseInstrumentation!!.onException(obj, e)
    }

    override fun sendStatus(resultCode: Int, results: Bundle) {
        mBaseInstrumentation?.sendStatus(resultCode, results)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun addResults(results: Bundle) {
        mBaseInstrumentation?.addResults(results)
    }

    override fun setAutomaticPerformanceSnapshots() {
        mBaseInstrumentation?.setAutomaticPerformanceSnapshots()
    }

    override fun startPerformanceSnapshot() {
        mBaseInstrumentation?.startPerformanceSnapshot()
    }

    override fun endPerformanceSnapshot() {
        mBaseInstrumentation?.endPerformanceSnapshot()
    }

    override fun onDestroy() {
        mBaseInstrumentation?.onDestroy()
    }

    override fun getContext(): Context {
        val context = mBaseInstrumentation!!.context
        logger.i("getContext#result = %s", context)
        return context
    }

    override fun getComponentName(): ComponentName {
        val componentName = mBaseInstrumentation!!.componentName
        logger.i("getComponentName#result = %s", componentName)
        return componentName
    }

    override fun getTargetContext(): Context {
        val context = mBaseInstrumentation!!.targetContext
        logger.i("getTargetContext#result = %s", context)
        return context
    }

    override fun isProfiling(): Boolean {
        val result = mBaseInstrumentation!!.isProfiling
        logger.i("isProfiling#result = %s", result)
        return result
    }

    override fun startProfiling() {
        mBaseInstrumentation?.startProfiling()
    }

    override fun stopProfiling() {
        mBaseInstrumentation?.stopProfiling()
    }

    override fun setInTouchMode(inTouch: Boolean) {
        mBaseInstrumentation?.setInTouchMode(inTouch)
    }

    override fun waitForIdle(recipient: Runnable) {
        logger.i("waitForIdle")
        mBaseInstrumentation?.waitForIdle(recipient)
    }

    override fun waitForIdleSync() {
        logger.i("waitForIdleSync")
        mBaseInstrumentation?.waitForIdleSync()
    }

    override fun runOnMainSync(runner: Runnable) {
        logger.i("runOnMainSync")
        mBaseInstrumentation?.runOnMainSync(runner)
    }

    override fun startActivitySync(intent: Intent): Activity {
        logger.i("startActivitySync#intent = %s", intent)
        return mBaseInstrumentation!!.startActivitySync(intent)
    }

    override fun addMonitor(monitor: ActivityMonitor) {
        mBaseInstrumentation?.addMonitor(monitor)
    }

    override fun addMonitor(filter: IntentFilter, result: ActivityResult, block: Boolean): ActivityMonitor {
        return mBaseInstrumentation!!.addMonitor(filter, result, block)
    }

    override fun addMonitor(cls: String, result: ActivityResult, block: Boolean): ActivityMonitor {
        return mBaseInstrumentation!!.addMonitor(cls, result, block)
    }

    override fun checkMonitorHit(monitor: ActivityMonitor, minHits: Int): Boolean {
        return mBaseInstrumentation!!.checkMonitorHit(monitor, minHits)
    }

    override fun waitForMonitor(monitor: ActivityMonitor): Activity {
        return mBaseInstrumentation!!.waitForMonitor(monitor)
    }

    override fun waitForMonitorWithTimeout(monitor: ActivityMonitor, timeOut: Long): Activity {
        logger.i("waitForMonitorWithTimeout#monitor = %s, timeOut = %s", monitor, timeOut)
        return mBaseInstrumentation!!.waitForMonitorWithTimeout(monitor, timeOut)
    }

    override fun removeMonitor(monitor: ActivityMonitor) {
        mBaseInstrumentation?.removeMonitor(monitor)
    }

    override fun invokeMenuActionSync(targetActivity: Activity, id: Int, flag: Int): Boolean {
        return mBaseInstrumentation!!.invokeMenuActionSync(targetActivity, id, flag)
    }

    override fun invokeContextMenuAction(targetActivity: Activity, id: Int, flag: Int): Boolean {
        return mBaseInstrumentation!!.invokeContextMenuAction(targetActivity, id, flag)
    }

    override fun sendStringSync(text: String) {
        mBaseInstrumentation?.sendStringSync(text)
    }

    override fun sendKeySync(event: KeyEvent) {
        mBaseInstrumentation?.sendKeySync(event)
    }

    override fun sendKeyDownUpSync(key: Int) {
        mBaseInstrumentation?.sendKeyDownUpSync(key)
    }

    override fun sendCharacterSync(keyCode: Int) {
        mBaseInstrumentation?.sendCharacterSync(keyCode)
    }

    override fun sendPointerSync(event: MotionEvent) {
        mBaseInstrumentation?.sendPointerSync(event)
    }

    override fun sendTrackballEventSync(event: MotionEvent) {
        mBaseInstrumentation?.sendTrackballEventSync(event)
    }

    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        logger.i("newApplication#cl = %s, className = %s, context = %s", cl, className, context)
        val application = mBaseInstrumentation!!.newApplication(cl, className, context)
        logger.i("newApplication#result = %s", application)
        //Debug.waitForDebugger();
        return application
    }

    override fun callApplicationOnCreate(app: Application?) {
        logger.i("callApplicationOnCreate#app = %s", app)
        mBaseInstrumentation?.callApplicationOnCreate(app)
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    override fun newActivity(
        clazz: Class<*>?, context: Context, token: IBinder, application: Application, intent: Intent,
        info: ActivityInfo, title: CharSequence, parent: Activity, id: String, lastNonConfigurationInstance: Any
    ): Activity {
        logger.i(
            "newActivity#class = %s, context = %s, token = %s, app = %s, intent = %s, info = %s, title = %s, parent = %s, id = %s," +
                    " lastNonConfigurationInstance = %s",
            clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance
        )
        val activity = mBaseInstrumentation!!.newActivity(clazz, context, token, application, intent,
            info, title, parent, id, lastNonConfigurationInstance
        )
        logger.i("newActivity#result = %s", activity)
        return activity
    }

    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    override fun newActivity(cl: ClassLoader, className: String, intent: Intent): Activity {
        logger.i("newActivity#classLoader = %s, className = %s, intent = %s", cl, className, intent)
        val activity = mBaseInstrumentation!!.newActivity(cl, className, intent)
        logger.i("newActivityInfo# result = %s", activity)
        return activity
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
        logger.i("callActivityOnCreate#activity = %s, bundle = %s", activity, icicle)
        if (BuildCompat.isAtLeastS){
            ComponentFixCompat.fixActivityWithOnCreate(activity)
        }
        val info = HActivity.mActivityInfo.get(activity)
        if (info.theme != 0) {
            activity.theme.applyStyle(info.theme, true)
        }else{
            val appInfo = HActivityInfo.applicationInfo.get(info)
            if (appInfo.theme != 0){
                activity.theme.applyStyle(appInfo.theme, true)
            }
        }
        ComponentFixCompat.fixActivityOrientation(activity)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        mBaseInstrumentation?.callActivityOnCreate(activity, icicle)
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?, persistentState: PersistableBundle?) {
        logger.i("callActivityOnCreate#activity = %s, bundle = %s, persistentState = %s", activity, icicle, persistentState)
        mBaseInstrumentation?.callActivityOnCreate(activity, icicle, persistentState)
    }

    override fun callActivityOnDestroy(activity: Activity) {
        logger.i("callActivityOnDestroy#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnDestroy(activity)
        // 销毁后如果如果没有Activity存在了，就把当前进程干掉
    }

    override fun callActivityOnRestoreInstanceState(activity: Activity, savedInstanceState: Bundle) {
        logger.i("callActivityOnRestoreInstanceState#activity = %s, savedInstanceState = %s", activity, savedInstanceState)
        mBaseInstrumentation?.callActivityOnRestoreInstanceState(activity, savedInstanceState)
    }

    override fun callActivityOnRestoreInstanceState(activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        logger.i(
            "callActivityOnRestoreInstanceState#activity = %s, savedInstanceState = %s, persistentState = %s",
            activity, savedInstanceState, persistentState
        )
        mBaseInstrumentation?.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState)
    }

    override fun callActivityOnPostCreate(activity: Activity, icicle: Bundle?) {
        logger.i("callActivityOnPostCreate#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnPostCreate(activity, icicle)
    }

    override fun callActivityOnPostCreate(activity: Activity, icicle: Bundle?, persistentState: PersistableBundle?) {
        logger.i("callActivityOnPostCreate#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnPostCreate(activity, icicle, persistentState)
    }

    override fun callActivityOnNewIntent(activity: Activity?, intent: Intent?) {
        logger.i("callActivityOnNewIntent#activity = %s, intent = %s", activity, intent)
        mBaseInstrumentation?.callActivityOnNewIntent(activity, intent)
    }

    override fun callActivityOnStart(activity: Activity) {
        logger.i("callActivityOnStart#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnStart(activity)
    }

    override fun callActivityOnRestart(activity: Activity) {
        logger.i("callActivityOnReStart#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnRestart(activity)
    }

    override fun callActivityOnResume(activity: Activity) {
        logger.i("callActivityOnResume#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnResume(activity)
    }

    override fun callActivityOnStop(activity: Activity) {
        logger.i("callActivityOnStop#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnStop(activity)
    }

    override fun callActivityOnSaveInstanceState(activity: Activity, outState: Bundle) {
        logger.i("callActivityOnSaveInstanceState#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnSaveInstanceState(activity, outState)
    }

    override fun callActivityOnSaveInstanceState(activity: Activity, outState: Bundle, outPersistentState: PersistableBundle) {
        logger.i("callActivityOnSaveInstanceState#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnSaveInstanceState(activity, outState, outPersistentState)
    }

    override fun callActivityOnPause(activity: Activity) {
        logger.i("callActivityOnPause#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnPause(activity)
    }

    override fun callActivityOnUserLeaving(activity: Activity) {
        logger.i("callActivityOnUserLeaving#activity = %s", activity)
        mBaseInstrumentation?.callActivityOnUserLeaving(activity)
    }

    override fun startAllocCounting() {
        mBaseInstrumentation?.startAllocCounting()
    }

    override fun stopAllocCounting() {
        mBaseInstrumentation?.stopAllocCounting()
    }

    override fun getAllocCounts(): Bundle {
        return mBaseInstrumentation!!.allocCounts
    }

    override fun getBinderCounts(): Bundle {
        return mBaseInstrumentation!!.binderCounts
    }

    override fun getUiAutomation(): UiAutomation {
        return mBaseInstrumentation!!.uiAutomation
    }

    @Throws(Throwable::class)
    open fun execStartActivity(
        context: Context, contextThread: IBinder,
        token: IBinder?, activity: Activity?, intent: Intent,
        requestCode: Int, options: Bundle?
    ): ActivityResult? {
        return invokeExecStartActivity(
            mBaseInstrumentation!!,
            Context::class.java,
            IBinder::class.java,
            IBinder::class.java,
            Activity::class.java,
            Intent::class.java,
            Integer.TYPE,
            Bundle::class.java
        ).call(mBaseInstrumentation, context, contextThread, token, activity, intent, requestCode, options)
    }

    @Throws(Throwable::class)
    open fun execStartActivity(
        context: Context,
        contextThread: IBinder,
        token: IBinder?,
        str: String?,
        intent: Intent,
        requestCode: Int,
        options: Bundle?
    ): ActivityResult? {
        return invokeExecStartActivity(
            mBaseInstrumentation!!,
            Context::class.java,
            IBinder::class.java,
            IBinder::class.java,
            String::class.java,
            Intent::class.java,
            Integer.TYPE,
            Bundle::class.java
        ).call(mBaseInstrumentation, context, contextThread, token, str, intent, requestCode, options)
    }

    @Throws(Throwable::class)
    open fun execStartActivity(
        context: Context,
        contextThread: IBinder,
        token: IBinder?,
        fragment: Fragment?,
        intent: Intent,
        requestCode: Int
    ): ActivityResult? {
        return invokeExecStartActivity(
            mBaseInstrumentation!!,
            Context::class.java,
            IBinder::class.java,
            IBinder::class.java,
            Fragment::class.java,
            Intent::class.java,
            Integer.TYPE
        ).call(mBaseInstrumentation, context, contextThread, token, fragment, intent, requestCode)
    }

    @Throws(Throwable::class)
    open fun execStartActivity(
        context: Context,
        contextThread: IBinder,
        token: IBinder?,
        activity: Activity?,
        intent: Intent,
        requestCode: Int
    ): ActivityResult? {
        return invokeExecStartActivity(
            mBaseInstrumentation!!,
            Context::class.java,
            IBinder::class.java,
            IBinder::class.java,
            Activity::class.java,
            Intent::class.java,
            Integer.TYPE
        ).call(mBaseInstrumentation, context, contextThread, token, activity, intent, requestCode)
    }

    @Throws(Throwable::class)
    open fun execStartActivity(
        context: Context,
        contextThread: IBinder,
        token: IBinder?,
        fragment: Fragment?,
        intent: Intent,
        requestCode: Int,
        bundle: Bundle?
    ): ActivityResult? {
        return invokeExecStartActivity(
            mBaseInstrumentation!!,
            Context::class.java,
            IBinder::class.java,
            IBinder::class.java,
            Fragment::class.java,
            Intent::class.java,
            Integer.TYPE,
            Bundle::class.java
        ).call(mBaseInstrumentation, context, contextThread, token, fragment, intent, requestCode, bundle)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Throws(Throwable::class)
    open fun execStartActivity(
        context: Context,
        contextThread: IBinder,
        token: IBinder?,
        activity: Activity?,
        intent: Intent,
        requestCode: Int,
        bundle: Bundle?,
        userHandle: UserHandle?
    ): ActivityResult? {
        return invokeExecStartActivity(
            mBaseInstrumentation!!,
            Context::class.java,
            IBinder::class.java,
            IBinder::class.java,
            Activity::class.java,
            Intent::class.java,
            Integer.TYPE,
            Bundle::class.java,
            UserHandle::class.java
        ).call(
            mBaseInstrumentation,
            *arrayOf(context, contextThread, token, activity, intent, requestCode, bundle, userHandle)
        ) as ActivityResult
    }

    companion object {
        protected val logger = getLogger(L.HOOK_TAG, "InstrumentationDelegate")
        const val TAG = "BaseInstrumentationDelegate"
        @Throws(NoSuchMethodException::class)
        private fun invokeExecStartActivity(obj: Any, vararg args: Class<*>): MethodWrapper<ActivityResult> {
            var cls: Class<*>? = obj.javaClass
            while (cls != null) {
                cls = try {
                    return MirrorReflection.on(obj.javaClass)
                        .method("execStartActivity", *args)
                } catch (e: Exception) {
                    cls.superclass
                }
            }
            throw NoSuchMethodException()
        }
    }
}