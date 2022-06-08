package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.reflect.android.hardware.location.HIContextHubService;

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
