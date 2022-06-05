package com.virtual.box.core.hook.service;

import android.os.ParcelFileDescriptor;

import androidx.annotation.Nullable;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HServiceManager;
import com.virtual.box.reflect.android.view.HIGraphicsStats;

public class IGraphicsStatsHookHandle extends BaseBinderHookHandle {

    public IGraphicsStatsHookHandle() {
        super("graphicsstats");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIGraphicsStats.Stub.asInterface.call(getOriginBinder());
    }

    /**
     *
     * @param callback IGraphicsStatsCallback
     */
    ParcelFileDescriptor requestBufferForProcess(MethodHandle methodHandle, String packageName, Object callback){
        return (ParcelFileDescriptor) methodHandle.invokeOriginMethod(new Object[]{
                hostPkg, callback
        });
    }
}
