package com.virtual.box.core.hook.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ParceledListSlice;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.content.pm.HIShortcutService;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HServiceManager;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class IShortcutServiceHookHandle extends BaseBinderHookHandle {

    public IShortcutServiceHookHandle() {
        super(Context.SHORTCUT_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIShortcutService.Stub.asInterface.call(getOriginBinder());
    }

    Object setDynamicShortcuts(MethodHandle methodHandle, String packageName,
                               ParceledListSlice shortcutInfoList, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcutInfoList, userId});
    }

    Object addDynamicShortcuts(MethodHandle methodHandle, String packageName,
                                      ParceledListSlice shortcutInfoList, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcutInfoList, userId});
    }

    Object removeDynamicShortcuts(MethodHandle methodHandle, String packageName, List shortcutIds, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcutIds, userId});
    }

    Object removeAllDynamicShortcuts(MethodHandle methodHandle, String packageName, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    Object updateShortcuts(MethodHandle methodHandle, String packageName, ParceledListSlice shortcuts,
                                  int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcuts, userId});
    }

    Object requestPinShortcut(MethodHandle methodHandle, String packageName, ShortcutInfo shortcut,
                                     IntentSender resultIntent, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcut, resultIntent, userId});
    }

    Object createShortcutResultIntent(MethodHandle methodHandle, String packageName, ShortcutInfo shortcut,
                                                     int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcut, userId});
    }

    Object disableShortcuts(MethodHandle methodHandle, String packageName, List shortcutIds,
                                   CharSequence disabledMessage, int disabledMessageResId, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, shortcutIds, disabledMessage, disabledMessageResId, userId
        });
    }

    Object enableShortcuts(MethodHandle methodHandle, String packageName, List shortcutIds, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcutIds, userId});
    }

    int getMaxShortcutCountPerActivity(MethodHandle methodHandle, String packageName, int userId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    int getRemainingCallCount(MethodHandle methodHandle, String packageName, int userId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    long getRateLimitResetTime(MethodHandle methodHandle, String packageName, int userId){
        return (long) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    int getIconMaxDimensions(MethodHandle methodHandle, String packageName, int userId){
        return (int) methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    }

    Object reportShortcutUsed(MethodHandle methodHandle, String packageName, String shortcutId, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcutId, userId});
    }

    Object onApplicationActive(MethodHandle methodHandle, String packageName, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, userId});
    } // system only API for sysUI


    // System API used by framework's ShareSheet (MethodHandle methodHandle, ChooserActivity)
    Object getShareTargets(MethodHandle methodHandle, String packageName, IntentFilter filter,
                                                     int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, filter, userId});
    }

    boolean hasShareTargets(MethodHandle methodHandle, String packageName, String packageToCheck, int userId){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{hostPkg, packageToCheck, userId});
    }

    Object removeLongLivedShortcuts(MethodHandle methodHandle, String packageName, List shortcutIds, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcutIds, userId});
    }

    Object getShortcuts(MethodHandle methodHandle, String packageName, int matchFlags, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, matchFlags, userId});
    }

    Object getDynamicShortcuts(MethodHandle methodHandle, String packageName, int matchFlags, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, matchFlags, userId});
    }

    Object pushDynamicShortcut(MethodHandle methodHandle, String packageName, ShortcutInfo shortcut, int userId){
        return methodHandle.invokeOriginMethod(new Object[]{hostPkg, shortcut, userId});
    }
}
