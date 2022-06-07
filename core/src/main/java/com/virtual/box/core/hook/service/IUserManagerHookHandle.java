package com.virtual.box.core.hook.service;

import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.UserManager;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.android.hardware.location.HIContextHubService;
import com.virtual.box.reflect.android.os.HIUserManager;

public class IUserManagerHookHandle extends BaseBinderHookHandle {
    public IUserManagerHookHandle() {
        super(Context.USER_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HIUserManager.Stub.asInterface.call(getOriginBinder());
    }

    void setApplicationRestrictions(MethodHandle methodHandle, String packageName, Bundle restrictions, int userId){
        methodHandle.invokeOriginMethod(new Object[]{ hostPkg, restrictions, userId });
    }
    Bundle getApplicationRestrictions(MethodHandle methodHandle, String packageName){
        return (Bundle) methodHandle.invokeOriginMethod(new Object[]{ hostPkg });
    }
    Bundle getApplicationRestrictionsForUser(MethodHandle methodHandle, String packageName, int userId){
        return (Bundle) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, userId });
    }

    boolean requestQuietModeEnabled(MethodHandle methodHandle, String callingPackage, boolean enableQuietMode, int userId, IntentSender target, int flags){
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{ hostPkg, enableQuietMode, userId });
    }

}
