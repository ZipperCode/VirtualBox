package com.virtual.box.core.hook.service;

import static com.virtual.box.reflect.android.HAttributionSourceState.packageName;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.base.util.log.L;
import com.virtual.box.base.util.log.Logger;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.display.HDisplayManagerGlobal;
import com.virtual.box.reflect.android.hardware.display.HIDisplayManager;
import com.virtual.box.reflect.android.os.HServiceManager;

public class AccessibilityManagerHookHandle extends BaseBinderHookHandle {

    private final Logger logger = Logger.getLogger(L.HOOK_TAG, "AccessibilityManagerHookHandle");

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

    /**
     * @param windowToken IWindow
     * @param connection  IAccessibilityInteractionConnection
     */
    int addAccessibilityInteractionConnection(MethodHandle methodHandle, Object windowToken, IBinder leashToken,
                                              Object connection,
                                              String packageName, int userId) {
        logger.i("addAccessibilityInteractionConnection# packageName = %s", packageName);
        return (int) methodHandle.invokeOriginMethod(new Object[]{
                windowToken, leashToken, connection, hostPkg, userId
        });
    }

    void temporaryEnableAccessibilityStateUntilKeyguardRemoved(
            MethodHandle methodHandle, ComponentName service,
            boolean touchExplorationEnabled) {
        String packageName = service.getPackageName();
        logger.i("addAccessibilityInteractionConnection# packageName = %s", packageName);
        methodHandle.invokeOriginMethod();
    }
}
