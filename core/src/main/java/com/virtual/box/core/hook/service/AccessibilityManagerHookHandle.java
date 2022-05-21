package com.virtual.box.core.hook.service;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.display.HDisplayManagerGlobal;
import com.virtual.box.reflect.android.hardware.display.HIDisplayManager;
import com.virtual.box.reflect.android.os.HServiceManager;

public class AccessibilityManagerHookHandle extends BaseBinderHookHandle{

    public AccessibilityManagerHookHandle() {
        super(Context.ACCESSIBILITY_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call(Context.ACCESSIBILITY_SERVICE);
        return HIDisplayManager.asInterface.call(binder);
    }

    @Override
    protected void hookInject(@NonNull Object target, @NonNull Object proxy) {
        super.hookInject(target, proxy);
        HDisplayManagerGlobal.mDm.set(HDisplayManagerGlobal.sInstance.call(), proxy);
    }

}
