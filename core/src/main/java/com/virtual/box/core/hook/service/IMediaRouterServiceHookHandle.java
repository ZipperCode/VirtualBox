package com.virtual.box.core.hook.service;

import android.content.Context;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.media.HIMediaRouterService;

public class IMediaRouterServiceHookHandle extends BaseBinderHookHandle {
    public IMediaRouterServiceHookHandle() {
        super(Context.MEDIA_ROUTER_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIMediaRouterService.Stub.asInterface.call(getOriginBinder());
    }

    /**
     * @param client IMediaRouterClient
     */
    void registerClientAsUser(MethodHandle methodHandle, Object client, String packageName, int userId) {
        methodHandle.invokeOriginMethod(new Object[]{
                client, hostPkg, userId
        });
    }

    /**
     * @param router IMediaRouter2
     */
    void registerRouter2(MethodHandle methodHandle, Object router, String packageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                router, hostPkg
        });
    }

    /**
     * @param manager IMediaRouter2Manager
     */
    void registerManager(MethodHandle methodHandle, Object manager, String packageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                manager, hostPkg
        });
    }

}
