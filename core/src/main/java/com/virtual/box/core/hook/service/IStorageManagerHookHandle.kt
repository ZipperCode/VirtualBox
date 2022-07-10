package com.virtual.box.core.hook.service;

import android.os.Process;
import android.os.storage.StorageVolume;

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

    /**
     * TODO Specified package com.sinyee.babybus.world under uid 10740 but it is really 10741
     * TODO 需要做uid处理
     */
    StorageVolume[] getVolumeList(MethodHandle methodHandle, int uid, String packageName, int flags){
        logger.i("getVolumeList#uid = %s, sysUid = %s, package = %s", uid, Process.myUid(), packageName);
        return (StorageVolume[]) methodHandle.invokeOriginMethod(new Object[]{
                Process.myUid(), hostPkg, flags
        });
    }
}
