package com.virtual.box.core.hook.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.PictureInPictureUiState;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.window.SplashScreenView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.hook.BaseHookHandle;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.app.HIActivityTaskManager;
import com.virtual.box.reflect.android.os.HServiceManager;

import java.util.List;

@TargetApi(Build.VERSION_CODES.P)
public class ActivityTaskManagerHookHandle extends BaseBinderHookHandle {

    public ActivityTaskManagerHookHandle() {
        super("activity_task");
    }

    @Nullable
    @Override
    protected Object initTargetObj() {
        return HIActivityTaskManager.Stub.asInterface.call(getProxyBinderObj());
    }

    @Override
    public boolean isSupport() {
        return HServiceManager.getService.call("activity_task") != this;
    }

    int startActivity(MethodHandle methodHandle, Object caller, String callingPackage,
                      String callingFeatureId, Intent intent, String resolvedType,
                      IBinder resultTo, String resultWho, int requestCode,
                      int flags, Object profilerInfo, Bundle options) {

    }

    int startActivities(Object caller, String callingPackage,
                        String callingFeatureId, Intent[] intents, String[] resolvedTypes,
                        IBinder resultTo, Bundle options, int userId) {
    }

    int startActivityAsUser(Object caller, String callingPackage,
                            String callingFeatureId, Intent intent, String resolvedType,
                            IBinder resultTo, String resultWho, int requestCode, int flags,
                            Object profilerInfo, Bundle options, int userId) {
    }

    boolean startNextMatchingActivity(IBinder callingActivity,
                                      Intent intent, Bundle options) {
    }

    boolean startDreamActivity(Intent intent) {
    }

    /**
     *
     * @param caller IApplicationThread
     * @param target IIntentSender
     */
    int startActivityIntentSender(Object caller,
                                  IIntentSender target, IBinder whitelistToken, Intent fillInIntent,
                                  String resolvedType, IBinder resultTo, String resultWho, int requestCode,
                                  int flagsMask, int flagsValues, Bundle options) {
    }

    /**
     *
     * @param caller IApplicationThread
     * @return WaitResult
     */
    Object startActivityAndWait(Object caller, String callingPackage,
                                    String callingFeatureId, Intent intent, String resolvedType,
                                    IBinder resultTo, String resultWho, int requestCode, int flags,
                                    Object profilerInfo, Bundle options, int userId) {
    }

    int startActivityWithConfig(Object caller, String callingPackage,
                                String callingFeatureId, Intent intent, String resolvedType,
                                IBinder resultTo, String resultWho, int requestCode, int startFlags,
                                Configuration newConfig, Bundle options, int userId) {
    }

    /**
     *
     * @param session IVoiceInteractionSession
     * @param interactor IVoiceInteractor
     */
    int startVoiceActivity(String callingPackage, String callingFeatureId, int callingPid,
                           int callingUid, Intent intent, String resolvedType,
                           Object session, Object interactor, int flags,
                           Object profilerInfo, Bundle options, int userId) {
    }

    int startAssistantActivity(String callingPackage, String callingFeatureId, int callingPid,
                               int callingUid, Intent intent, String resolvedType, Bundle options, int userId) {
    }

    /**
     *
     * @param recentsAnimationRunner IRecentsAnimationRunner
     */
    void startRecentsActivity(Intent intent, long eventTime,
                              Object recentsAnimationRunner) {
    }

    int startActivityFromRecents(int taskId, Bundle options) {
    }

    int startActivityAsCaller(Object caller, String callingPackage,
                              Intent intent, String resolvedType, IBinder resultTo, String resultWho,
                              int requestCode, int flags, Object profilerInfo, Bundle options,
                              IBinder permissionToken, boolean ignoreTargetSecurity, int userId) {
    }

    boolean isActivityStartAllowedOnDisplay(int displayId, Intent intent, String resolvedType,
                                            int userId) {
    }

    /**
     *
     * @param app IApplicationThread
     */
    void moveTaskToFront(Object app, String callingPackage, int task,
                         int flags, Bundle options) {
    }


    List<IBinder> getAppTasks(String callingPackage) {
    }

    /**
     * @param receiver IAssistDataReceiver
     */
    boolean requestAssistDataForTask(Object receiver, int taskId,
                                     String callingPackageName) {
    }



    /**
     * Registers a remote animation to be run for all activity starts from a certapackage during
     * a short predefined amount of time.
     * @param adapter RemoteAnimationAdapter
     */
    void registerRemoteAnimationForNextActivityStart(String packageName,
                                                     Object adapter) {
    }

    int getPackageScreenCompatMode(String packageName) {
    }

    void setPackageScreenCompatMode(String packageName, int mode) {
    }

    boolean getPackageAskScreenCompat(String packageName) {
    }

    void setPackageAskScreenCompat(String packageName, boolean ask) {
    }

}
