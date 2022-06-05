package com.virtual.box.core.hook.service;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.service.autofill.UserData;
import android.support.v4.os.IResultReceiver;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.hook.BaseHookHandle;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.os.HServiceManager;
import com.virtual.box.reflect.android.view.autofill.HIAutoFillManager;

import java.util.List;

public class AutofillManagerHookHandle extends BaseBinderHookHandle {
    public AutofillManagerHookHandle() {
        super("autofill");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIAutoFillManager.Stub.asInterface.call(getOriginBinder());
    }

    @Override
    public boolean isHooked() {
        return HServiceManager.getService.call(getServiceName()) == this;
    }

    /**
     *
     * @param client IAutoFillManagerClient
     */
    void addClient(MethodHandle methodHandle, Object client, ComponentName componentName, int userId,
                   IResultReceiver result) {
        methodHandle.invokeOriginMethod();
    }


    void startSession(MethodHandle methodHandle, IBinder activityToken, IBinder appCallback, AutofillId autoFillId,
                      Rect bounds, AutofillValue value, int userId, boolean hasCallback, int flags,
                      ComponentName componentName, boolean compatMode, IResultReceiver result) {
        methodHandle.invokeOriginMethod();
    }

    void isServiceEnabled(MethodHandle methodHandle, int userId, String packageName, IResultReceiver result) {
        methodHandle.invokeOriginMethod();
    }

}
