package com.virtual.box.core.hook.service;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallerCallback;
import android.content.pm.PackageInstaller;
import android.content.pm.ParceledListSlice;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.content.pm.HILauncherApps;
import com.virtual.box.reflect.android.os.HServiceManager;

import java.util.List;

/**
 * @author zipper
 */
public class LauncherAppsHookHandle extends BaseBinderHookHandle {

    public LauncherAppsHookHandle() {
        super(Context.LAUNCHER_APPS_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call(Context.LAUNCHER_APPS_SERVICE);
        return HILauncherApps.Stub.asInterface.call(binder);
    }

    /**
     * @param listener IOnAppsChangedListener
     */
    void addOnAppsChangedListener(MethodHandle methodHandle, String callingPackage, IInterface listener) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, listener});
    }

    ParceledListSlice<?> getLauncherActivities(MethodHandle methodHandle,
                                               String callingPackage, String packageName, UserHandle user) {
        // TODO
        return (ParceledListSlice<?>) methodHandle.invokeOriginMethod();
    }

    Object resolveLauncherActivityInternal(MethodHandle methodHandle,
                                                                 String callingPackage, ComponentName component, UserHandle user) {
        // TODO
        return methodHandle.invokeOriginMethod();
    }

    /**
     * @param caller IApplicationThread
     */
    void startSessionDetailsActivityAsUser(MethodHandle methodHandle, IInterface caller, String callingPackage,
                                           String callingFeatureId, PackageInstaller.SessionInfo sessionInfo,
                                           Rect sourceBounds, Bundle opts, UserHandle user) {
        methodHandle.invokeOriginMethod(new Object[]{
                caller, hostPkg, callingFeatureId, sessionInfo, sourceBounds, opts, user
        });
    }

    /**
     * @param caller IApplicationThread
     */
    void startActivityAsUser(MethodHandle methodHandle, IInterface caller, String callingPackage,
                             String callingFeatureId, ComponentName component, Rect sourceBounds,
                             Bundle opts, UserHandle user) {
        // TODO
        methodHandle.invokeOriginMethod(new Object[]{
                caller, hostPkg, callingFeatureId, component, sourceBounds, opts, user
        });
    }

    PendingIntent getActivityLaunchIntent(MethodHandle methodHandle, ComponentName component, Bundle opts,
                                          UserHandle user) {
        // TODO
        return (PendingIntent) methodHandle.invokeOriginMethod();
    }

    /**
     * @param caller IApplicationThread
     */
    void showAppDetailsAsUser(MethodHandle methodHandle, IInterface caller, String callingPackage,
                              String callingFeatureId, ComponentName component, Rect sourceBounds,
                              Bundle opts, UserHandle user) {
        // TODO
        methodHandle.invokeOriginMethod();
    }

    boolean isPackageEnabled(MethodHandle methodHandle, String callingPackage, String packageName, UserHandle user) {
        // TODO
        return (boolean) methodHandle.invokeOriginMethod();
    }

    Bundle getSuspendedPackageLauncherExtras(MethodHandle methodHandle, String packageName, UserHandle user) {
        // TODO
        return (Bundle) methodHandle.invokeOriginMethod();
    }

    boolean isActivityEnabled(MethodHandle methodHandle,
                              String callingPackage, ComponentName component, UserHandle user) {
        // TODO
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, component, user
        });
    }

    ApplicationInfo getApplicationInfo(MethodHandle methodHandle,
                                       String callingPackage, String packageName, int flags, UserHandle user) {
        // TODO
        return (ApplicationInfo) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, hostPkg, flags, user
        });
    }

    /**
     * @return LauncherApps.AppUsageLimit
     */
    Object getAppUsageLimit(MethodHandle methodHandle, String callingPackage, String packageName,
                            UserHandle user) {
        // TODO
        return methodHandle.invokeOriginMethod();
    }

    ParceledListSlice<?> getShortcuts(MethodHandle methodHandle, String callingPackage, Object query,
                                      UserHandle user) {
        return (ParceledListSlice<?>) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, query, user
        });
    }

    void pinShortcuts(MethodHandle methodHandle, String callingPackage, String packageName, List<String> shortcutIds,
                      UserHandle user) {
        // TODO
        methodHandle.invokeOriginMethod();
    }

    boolean startShortcut(MethodHandle methodHandle, String callingPackage, String packageName, String featureId, String id,
                          Rect sourceBounds, Bundle startActivityOptions, int userId) {
        return (boolean) methodHandle.invokeOriginMethod();
    }

    int getShortcutIconResId(MethodHandle methodHandle, String callingPackage, String packageName, String id,
                             int userId) {
        return (int) methodHandle.invokeOriginMethod();
    }

    ParcelFileDescriptor getShortcutIconFd(MethodHandle methodHandle, String callingPackage, String packageName, String id,
                                           int userId) {
        return (ParcelFileDescriptor) methodHandle.invokeOriginMethod();
    }

    boolean hasShortcutHostPermission(MethodHandle methodHandle, String callingPackage) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg
        });
    }

    boolean shouldHideFromSuggestions(MethodHandle methodHandle, String packageName, UserHandle user) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, user});
    }

    ParceledListSlice getShortcutConfigActivities(MethodHandle methodHandle,
                                                  String callingPackage, String packageName, UserHandle user) {
        // TODO
        return (ParceledListSlice) methodHandle.invokeOriginMethod();
    }

    IntentSender getShortcutConfigActivityIntent(MethodHandle methodHandle, String callingPackage, ComponentName component,
                                                 UserHandle user) {
        return (IntentSender) methodHandle.invokeOriginMethod();
    }

    PendingIntent getShortcutIntent(MethodHandle methodHandle, String callingPackage, String packageName, String shortcutId,
                                    Bundle opts, UserHandle user) {
        return (PendingIntent) methodHandle.invokeOriginMethod();
    }

    // Unregister is performed using package installer
    void registerPackageInstallerCallback(MethodHandle methodHandle, String callingPackage,
                                          IPackageInstallerCallback callback) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, callback
        });
    }

    ParceledListSlice getAllSessions(MethodHandle methodHandle, String callingPackage) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    /**
     * @param callback IShortcutChangeCallback
     */
    void registerShortcutChangeCallback(MethodHandle methodHandle, String callingPackage, Object query,
                                        IInterface callback) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, query, callback
        });
    }

    /**
     * @param callback IShortcutChangeCallback
     */
    void unregisterShortcutChangeCallback(MethodHandle methodHandle, String callingPackage,
                                          IInterface callback) {
        methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, callback
        });
    }

    void cacheShortcuts(MethodHandle methodHandle, String callingPackage, String packageName, List<String> shortcutIds,
                        UserHandle user, int cacheFlags) {
        // TODO
        methodHandle.invokeOriginMethod();
    }

    void uncacheShortcuts(MethodHandle methodHandle, String callingPackage, String packageName, List<String> shortcutIds,
                          UserHandle user, int cacheFlags) {
        // TODO
        methodHandle.invokeOriginMethod();
    }

    String getShortcutIconUri(MethodHandle methodHandle, String callingPackage, String packageName, String shortcutId,
                              int userId) {
        // TODO
        return (String) methodHandle.invokeOriginMethod();
    }
}
