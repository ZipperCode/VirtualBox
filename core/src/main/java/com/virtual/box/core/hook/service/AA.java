package com.virtual.box.core.hook.service;

import android.os.IBinder;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.os.HIDeviceIdentifiersPolicyService;
import com.virtual.box.reflect.android.os.HServiceManager;

public class AA extends BaseBinderHookHandle{

    public AA() {
        super("device_identifiers");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call("device_identifiers");
        return HIDeviceIdentifiersPolicyService.Stub.asInterface.call(binder);
    }

    String getSerialForPackage(MethodHandle methodHandle, String callingPackage, String callingFeatureId){
        return (String) methodHandle.invokeOriginMethod(new Object[]{hostPkg, callingFeatureId});
    }
}
