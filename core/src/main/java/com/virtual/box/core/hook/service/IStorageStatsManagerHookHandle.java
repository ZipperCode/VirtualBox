package com.virtual.box.core.hook.service;

import android.app.usage.ExternalStorageStats;
import android.app.usage.StorageStats;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.app.usage.HIStorageStatsManager;

@RequiresApi(api = Build.VERSION_CODES.O)
public class IStorageStatsManagerHookHandle extends BaseBinderHookHandle {

    public IStorageStatsManagerHookHandle() {
        super(Context.STORAGE_STATS_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIStorageStatsManager.Stub.asInterface.call(getOriginBinder());
    }

    boolean isQuotaSupported(MethodHandle methodHandle, String volumeUuid, String callingPackage) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg});
    }

    boolean isReservedSupported(MethodHandle methodHandle, String volumeUuid, String callingPackage) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg});
    }

    long getTotalBytes(MethodHandle methodHandle, String volumeUuid, String callingPackage) {
        return (long) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg});
    }

    long getFreeBytes(MethodHandle methodHandle, String volumeUuid, String callingPackage) {
        return (long) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg});
    }

    long getCacheBytes(MethodHandle methodHandle, String volumeUuid, String callingPackage) {
        return (long) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg});
    }

    long getCacheQuotaBytes(MethodHandle methodHandle, String volumeUuid, int uid, String callingPackage) {
        return (long) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, uid, hostPkg});
    }

    StorageStats queryStatsForPackage(MethodHandle methodHandle, String volumeUuid, String packageName,
                                      int userId, String callingPackage) {
        return (StorageStats) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg, userId, hostPkg});
    }

    StorageStats queryStatsForUid(MethodHandle methodHandle, String volumeUuid, int uid, String callingPackage) {
        return (StorageStats) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, uid, hostPkg});
    }

    StorageStats queryStatsForUser(MethodHandle methodHandle, String volumeUuid, int userId, String callingPackage) {
        return (StorageStats) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, userId, hostPkg});
    }

    ExternalStorageStats queryExternalStatsForUser(MethodHandle methodHandle, String volumeUuid, int userId, String callingPackage) {
        return (ExternalStorageStats) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, userId, hostPkg});
    }

    ParceledListSlice /* CrateInfo */ queryCratesForPackage(MethodHandle methodHandle, String volumeUuid, String packageName,
                                                            int userId, String callingPackage) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{volumeUuid, hostPkg, userId, hostPkg});
    }

    ParceledListSlice /* CrateInfo */ queryCratesForUid(MethodHandle methodHandle, String volumeUuid, int uid,
                                                        String callingPackage) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{
                volumeUuid, uid, hostPkg
        });
    }

    ParceledListSlice /* CrateInfo */ queryCratesForUser(MethodHandle methodHandle, String volumeUuid, int userId,
                                                         String callingPackage) {
        return (ParceledListSlice) methodHandle.invokeOriginMethod(new Object[]{
                volumeUuid, userId, hostPkg
        });
    }
}
