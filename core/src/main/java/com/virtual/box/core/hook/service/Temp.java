package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HServiceManager;

public class Temp extends BaseBinderHookHandle {
    public Temp() {
        super("contexthub_service");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIContextHubService.Stub.asInterface.call(getOriginBinder());
    }
}
