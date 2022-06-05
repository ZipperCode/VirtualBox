package com.virtual.box.core.hook.service;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.v4.os.IResultReceiver;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;

import androidx.annotation.Nullable;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.base.util.log.L;
import com.virtual.box.base.util.log.Logger;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HServiceManager;
import com.virtual.box.reflect.android.view.autofill.HIAutoFillManager;

public class ContextHubServiceHookHandle extends BaseBinderHookHandle {

    private final Logger logger = Logger.getLogger(L.HOOK_TAG,"ContextHubServiceHookHandle");

    public ContextHubServiceHookHandle() {
        super(BuildCompat.isAtLeastOreo() ? "contexthub" : "contexthub_service");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIContextHubService.Stub.asInterface.call(getOriginBinder());
    }

    @Override
    public boolean isHooked() {
        return HServiceManager.getService.call(getServiceName()) == this;
    }

    /**
     * IContextHubClient
     * @param client IContextHubClientCallback
     * @return IContextHubClient
     */
    Object createClient(MethodHandle methodHandle,
            int contextHubId, Object client, String attributionTag,
            String packageName){
        logger.i("createClient#package = %s", packageName);
        return methodHandle.invokeOriginMethod(new Object[]{
                contextHubId, client, attributionTag, hostPkg
        });
    }
}
