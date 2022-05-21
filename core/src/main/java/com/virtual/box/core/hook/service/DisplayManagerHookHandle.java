package com.virtual.box.core.hook.service;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.IBinder;
import android.os.IInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.display.HDisplayManagerGlobal;
import com.virtual.box.reflect.android.hardware.display.HIDisplayManager;
import com.virtual.box.reflect.android.os.HIDeviceIdentifiersPolicyService;
import com.virtual.box.reflect.android.os.HServiceManager;

public class DisplayManagerHookHandle extends BaseBinderHookHandle{

    public DisplayManagerHookHandle() {
        super(Context.DISPLAY_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call(Context.DISPLAY_SERVICE);
        return HIDisplayManager.asInterface.call(binder);
    }

    @Override
    protected void hookInject(@NonNull Object target, @NonNull Object proxy) {
        super.hookInject(target, proxy);
        HDisplayManagerGlobal.mDm.set(HDisplayManagerGlobal.sInstance.call(), proxy);
    }

    /**
     * Requires CAPTURE_VIDEO_OUTPUT, CAPTURE_SECURE_VIDEO_OUTPUT, or an appropriate
     * MediaProjection token for certacombinations of flags.
     * @param virtualDisplayConfig VirtualDisplayConfig
     * @param callback  IVirtualDisplayCallback
     * @param projectionToken IMediaProjection
     */
    int createVirtualDisplay(MethodHandle methodHandle, Object virtualDisplayConfig,
                             Object callback, Object projectionToken,
                             String packageName){
        return (int) methodHandle.invokeOriginMethod(new Object[]{virtualDisplayConfig, callback, projectionToken, hostPkg});
    }

    /**
     * Requires BRIGHTNESS_SLIDER_USAGE permission.
     */
    ParceledListSlice<?> getBrightnessEvents(MethodHandle methodHandle, String callingPackage){
        return (ParceledListSlice<?>) methodHandle.invokeOriginMethod(new Object[]{hostPkg});
    }

    /**
     * Sets the global brightness configuration for a given user. Requires
     * CONFIGURE_DISPLAY_BRIGHTNESS, and INTERACT_ACROSS_USER if the user being configured is not
     * the same as the calling user.
     * @param c BrightnessConfiguration
     */
    void setBrightnessConfigurationForUser(MethodHandle methodHandle, Object c, int userId,
                                           String packageName){
        methodHandle.invokeOriginMethod(new Object[]{c, userId, hostPkg});
    }

    /**
     * Sets the global brightness configuration for a given display. Requires
     * CONFIGURE_DISPLAY_BRIGHTNESS.
     * @param c BrightnessConfiguration
     */
    void setBrightnessConfigurationForDisplay(MethodHandle methodHandle, Object c, String uniqueDisplayId,
                                              int userId, String packageName){
        methodHandle.invokeOriginMethod(new Object[]{c, uniqueDisplayId, userId, hostPkg});
    }
}
