package com.virtual.box.core.hook.service;

import android.content.Context;
import android.os.IBinder;
import android.os.WorkSource;

import androidx.annotation.Nullable;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HIPowerManager;
import com.virtual.box.reflect.android.os.HServiceManager;

public class IPowerManagerHookHandle extends BaseBinderHookHandle {
    public IPowerManagerHookHandle() {
        super(Context.POWER_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIPowerManager.Stub.asInterface.call(getOriginBinder());
    }

    void acquireWakeLock(MethodHandle methodHandle, IBinder lock, int flags, String tag, String packageName, WorkSource ws,
                         String historyTag, int displayId){
        methodHandle.invokeOriginMethod();
    }
    void acquireWakeLockWithUid(MethodHandle methodHandle, IBinder lock, int flags, String tag, String packageName,
                                int uidtoblame, int displayId){
        methodHandle.invokeOriginMethod();
    }

    void wakeUp(MethodHandle methodHandle, long time, int reason, String details, String opPackageName){
        methodHandle.invokeOriginMethod();
    }

    // Do not use, will be deprecated soon.  b/151831987
    void acquireWakeLockAsync(MethodHandle methodHandle, IBinder lock, int flags, String tag, String packageName,
                                     WorkSource ws, String historyTag){
        methodHandle.invokeOriginMethod();
    }

}
