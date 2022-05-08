package com.virtual.box.core.hook.delegate;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.virtual.box.base.util.log.L;
import com.virtual.box.reflect.MirrorReflection;

public abstract class BaseInstrumentationDelegate extends Instrumentation {

    public static final String TAG = "BaseInstrumentationDelegate";

    protected Instrumentation mBaseInstrumentation;

    /**
     * ActivityThread#handleBindApplication中调用
     */
    @Override
    public void onCreate(Bundle arguments) {
        mBaseInstrumentation.onCreate(arguments);
    }

    @Override
    public void start() {
        mBaseInstrumentation.start();
    }

    @Override
    public void onStart() {
        mBaseInstrumentation.onStart();
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        return mBaseInstrumentation.onException(obj, e);
    }

    @Override
    public void sendStatus(int resultCode, Bundle results) {
        mBaseInstrumentation.sendStatus(resultCode, results);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addResults(Bundle results) {
        mBaseInstrumentation.addResults(results);
    }

    @Override
    public void setAutomaticPerformanceSnapshots() {
        mBaseInstrumentation.setAutomaticPerformanceSnapshots();
    }

    @Override
    public void startPerformanceSnapshot() {
        mBaseInstrumentation.startPerformanceSnapshot();
    }

    @Override
    public void endPerformanceSnapshot() {
        mBaseInstrumentation.endPerformanceSnapshot();
    }

    @Override
    public void onDestroy() {
        mBaseInstrumentation.onDestroy();
    }

    @Override
    public Context getContext() {
        Context context =  mBaseInstrumentation.getContext();
        L.hd("[%s] >> getContext >> result = %s", TAG, context);
        return context;
    }

    @Override
    public ComponentName getComponentName() {
        ComponentName componentName = mBaseInstrumentation.getComponentName();
        L.hd("[%s] >> getComponentName >> result = %s", TAG, componentName);
        return componentName;
    }

    @Override
    public Context getTargetContext() {
        Context context = mBaseInstrumentation.getTargetContext();
        L.hd("[%s] >> getTargetContext >> result = %s", TAG, context);
        return context;
    }

    @Override
    public boolean isProfiling() {
        return mBaseInstrumentation.isProfiling();
    }

    @Override
    public void startProfiling() {
        mBaseInstrumentation.startProfiling();
    }

    @Override
    public void stopProfiling() {
        mBaseInstrumentation.stopProfiling();
    }

    @Override
    public void setInTouchMode(boolean inTouch) {
        mBaseInstrumentation.setInTouchMode(inTouch);
    }

    @Override
    public void waitForIdle(Runnable recipient) {
        mBaseInstrumentation.waitForIdle(recipient);
    }

    @Override
    public void waitForIdleSync() {
        mBaseInstrumentation.waitForIdleSync();
    }

    @Override
    public void runOnMainSync(Runnable runner) {
        mBaseInstrumentation.runOnMainSync(runner);
    }

    @Override
    public Activity startActivitySync(Intent intent) {
        return mBaseInstrumentation.startActivitySync(intent);
    }

    @Override
    public void addMonitor(ActivityMonitor monitor) {
        mBaseInstrumentation.addMonitor(monitor);
    }

    @Override
    public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
        return mBaseInstrumentation.addMonitor(filter, result, block);
    }

    @Override
    public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
        return mBaseInstrumentation.addMonitor(cls, result, block);
    }

    @Override
    public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
        return mBaseInstrumentation.checkMonitorHit(monitor, minHits);
    }

    @Override
    public Activity waitForMonitor(ActivityMonitor monitor) {
        return mBaseInstrumentation.waitForMonitor(monitor);
    }

    @Override
    public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
        return mBaseInstrumentation.waitForMonitorWithTimeout(monitor, timeOut);
    }

    @Override
    public void removeMonitor(ActivityMonitor monitor) {
        mBaseInstrumentation.removeMonitor(monitor);
    }

    @Override
    public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
        return mBaseInstrumentation.invokeMenuActionSync(targetActivity, id, flag);
    }

    @Override
    public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
        return mBaseInstrumentation.invokeContextMenuAction(targetActivity, id, flag);
    }

    @Override
    public void sendStringSync(String text) {
        mBaseInstrumentation.sendStringSync(text);
    }

    @Override
    public void sendKeySync(KeyEvent event) {
        mBaseInstrumentation.sendKeySync(event);
    }

    @Override
    public void sendKeyDownUpSync(int key) {
        mBaseInstrumentation.sendKeyDownUpSync(key);
    }

    @Override
    public void sendCharacterSync(int keyCode) {
        mBaseInstrumentation.sendCharacterSync(keyCode);
    }

    @Override
    public void sendPointerSync(MotionEvent event) {
        mBaseInstrumentation.sendPointerSync(event);
    }

    @Override
    public void sendTrackballEventSync(MotionEvent event) {
        mBaseInstrumentation.sendTrackballEventSync(event);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Application application = mBaseInstrumentation.newApplication(cl, className, context);
        L.hdParamTag(TAG, "cl = %s, className = %s, context = %s, result = %s",cl, className, context, application);
        //Debug.waitForDebugger();
        return application;
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        L.hd("[%s] >> callApplicationOnCreate >> app = %s", TAG, app);
        mBaseInstrumentation.callApplicationOnCreate(app);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent,
                                ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
        Activity activity =  mBaseInstrumentation.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        L.hdParamTag(TAG,"clazz = %s, context = %s, token = %s, application = %s, intent = %s, info = %s, title = %s, parent = %s, id = %s," +
                        " lastNonConfigurationInstance = %s",
                clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        return activity;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Activity activity =  mBaseInstrumentation.newActivity(cl, className, intent);
        L.hdParamTag(TAG,"classLoader = %s, className = %s, intent = %s, result = %s", cl, className, intent, activity);
        return activity;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
//        Debug.waitForDebugger();
        L.hdParamTag(TAG,"activity = %s, bundle = %s", activity, icicle);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mBaseInstrumentation.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        mBaseInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        L.hdParamTag(TAG,"activity = %s", activity);
        mBaseInstrumentation.callActivityOnDestroy(activity);
        // 销毁后如果如果没有Activity存在了，就把当前进程干掉
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        mBaseInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        mBaseInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        mBaseInstrumentation.callActivityOnPostCreate(activity, icicle);
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        mBaseInstrumentation.callActivityOnPostCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        mBaseInstrumentation.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        mBaseInstrumentation.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        mBaseInstrumentation.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        mBaseInstrumentation. callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        mBaseInstrumentation.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        mBaseInstrumentation.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        mBaseInstrumentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        mBaseInstrumentation.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        mBaseInstrumentation.callActivityOnUserLeaving(activity);
    }

    @Override
    public void startAllocCounting() {
        mBaseInstrumentation.startAllocCounting();
    }

    @Override
    public void stopAllocCounting() {
        mBaseInstrumentation.stopAllocCounting();
    }

    @Override
    public Bundle getAllocCounts() {
        return mBaseInstrumentation.getAllocCounts();
    }

    @Override
    public Bundle getBinderCounts() {
        return mBaseInstrumentation.getBinderCounts();
    }

    @Override
    public UiAutomation getUiAutomation() {
        return mBaseInstrumentation.getUiAutomation();
    }

    public ActivityResult execStartActivity(Context context, IBinder contextThread, IBinder token, Activity activity, Intent intent, int requestCode, Bundle options) throws Throwable {
        return invokeExecStartActivity(mBaseInstrumentation,
                Context.class,
                IBinder.class,
                IBinder.class,
                Activity.class,
                Intent.class,
                Integer.TYPE,
                Bundle.class).call(mBaseInstrumentation, context, contextThread, token, activity, intent, requestCode, options);
    }

    public ActivityResult execStartActivity(Context context, IBinder contextThread, IBinder token, String str, Intent intent, int requestCode, Bundle options) throws Throwable {
        return invokeExecStartActivity(mBaseInstrumentation,
                Context.class,
                IBinder.class,
                IBinder.class,
                String.class,
                Intent.class,
                Integer.TYPE,
                Bundle.class).call(mBaseInstrumentation, context, contextThread, token, str, intent, requestCode, options);
    }

    public ActivityResult execStartActivity(Context context, IBinder contextThread, IBinder token, Fragment fragment, Intent intent, int requestCode) throws Throwable {
        return invokeExecStartActivity(mBaseInstrumentation,
                Context.class,
                IBinder.class,
                IBinder.class,
                Fragment.class,
                Intent.class,
                Integer.TYPE).call(mBaseInstrumentation, context, contextThread, token, fragment, intent, requestCode);
    }

    public ActivityResult execStartActivity(Context context, IBinder contextThread, IBinder token, Activity activity, Intent intent, int requestCode) throws Throwable {
        return invokeExecStartActivity(mBaseInstrumentation,
                Context.class,
                IBinder.class,
                IBinder.class,
                Activity.class,
                Intent.class,
                Integer.TYPE).call(mBaseInstrumentation, context, contextThread, token, activity, intent, requestCode);
    }

    public ActivityResult execStartActivity(Context context, IBinder contextThread, IBinder token, Fragment fragment, Intent intent, int requestCode, Bundle bundle) throws Throwable {
        return invokeExecStartActivity(mBaseInstrumentation,
                Context.class,
                IBinder.class,
                IBinder.class,
                Fragment.class,
                Intent.class,
                Integer.TYPE,
                Bundle.class).call(mBaseInstrumentation, context, contextThread, token, fragment, intent, requestCode, bundle);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ActivityResult execStartActivity(Context context, IBinder contextThread, IBinder token, Activity activity, Intent intent, int requestCode, Bundle bundle, UserHandle userHandle) throws Throwable {
        return (ActivityResult) invokeExecStartActivity(mBaseInstrumentation,
                Context.class,
                IBinder.class,
                IBinder.class,
                Activity.class,
                Intent.class,
                Integer.TYPE,
                Bundle.class,
                UserHandle.class).call(mBaseInstrumentation, new Object[]{context, contextThread, token, activity, intent, requestCode, bundle, userHandle});
    }

    private static MirrorReflection.MethodWrapper<ActivityResult> invokeExecStartActivity(Object obj, Class<?>... args) throws NoSuchMethodException {
        Class<?> cls = obj.getClass();
        while (cls != null) {
            try {
                return MirrorReflection.on(obj.getClass())
                        .method("execStartActivity", args);
            } catch (Exception e) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchMethodException();
    }
}
