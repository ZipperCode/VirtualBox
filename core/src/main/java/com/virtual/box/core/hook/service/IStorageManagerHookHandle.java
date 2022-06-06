package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.base.util.compat.BuildCompat;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HServiceManager;
import com.virtual.box.reflect.android.os.storage.HIStorageManager;

public class IStorageManagerHookHandle extends BaseBinderHookHandle {
    public IStorageManagerHookHandle() {
        super("mount");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIStorageManager.Stub.asInterface.call(getOriginBinder());
    }

}
