package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.base.util.log.L;
import com.virtual.box.base.util.log.Logger;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.os.storage.HIStorageManager;

public class IStorageManagerHookHandle extends BaseBinderHookHandle {
    private final Logger logger = Logger.getLogger(L.HOOK_TAG,"IStorageManagerHookHandle");

    public IStorageManagerHookHandle() {
        super("mount");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIStorageManager.Stub.asInterface.call(getOriginBinder());
    }

    void mkdirs(MethodHandle methodHandle, String callingPkg, String path){
        logger.i("mkdirs#callingPkg = %s, path = %s", callingPkg, path);
        methodHandle.invokeOriginMethod();
    }
}
