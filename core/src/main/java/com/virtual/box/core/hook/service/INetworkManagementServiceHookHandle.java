package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.reflect.android.os.HINetworkManagementService;

public class INetworkManagementServiceHookHandle extends BaseBinderHookHandle {
    public INetworkManagementServiceHookHandle() {
        super("network_management");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HINetworkManagementService.Stub.asInterface.call(getOriginBinder());
    }
}
