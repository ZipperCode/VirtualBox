package com.virtual.box.core.hook.service;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.media.HIMediaProjectionManager;
import com.virtual.box.reflect.android.os.HIDeviceIdentifiersPolicyService;
import com.virtual.box.reflect.android.os.HServiceManager;

public class MediaProjectionManagerHookHandle extends BaseBinderHookHandle{

    public MediaProjectionManagerHookHandle() {
        super(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call(Context.MEDIA_PROJECTION_SERVICE);
        return HIMediaProjectionManager.Stub.asInterface.call(binder);
    }

    boolean hasProjectionPermission(MethodHandle methodHandle, int uid, String packageName){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{uid, hostPkg});
    }
    IInterface createProjection(MethodHandle methodHandle, int uid, String packageName, int type,
                                boolean permanentGrant){
        return (IInterface) methodHandle.invokeOriginMethod(new Object[]{uid, packageName, type, permanentGrant});
    }
}
