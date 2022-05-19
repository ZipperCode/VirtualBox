package com.virtual.box.core.hook.service;

import android.content.Context;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.core.manager.ServiceManager;
import com.virtual.box.reflect.android.os.HServiceManager;
import com.virtual.box.reflect.com.android.internal.app.HIAppOpsService;

/**
 * @author zhangzhipeng
 * @date 2022/5/19
 **/
public class AppOpsManagerHookHandle extends BaseBinderHookHandle{

    public AppOpsManagerHookHandle() {
        super(Context.APP_OPS_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder call = HServiceManager.getService.call(Context.APP_OPS_SERVICE);
        return HIAppOpsService.Stub.asInterface.call(call);
    }

    @Override
    public boolean isHooked() {
        return getOriginObject() != getProxyInvocation();
    }

    int checkOperation(MethodHandle methodHandle, int code, int uid, String packageName){
        return (int) methodHandle.invokeOriginMethod();
    }
}
