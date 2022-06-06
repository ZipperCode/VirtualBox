package com.virtual.box.core.hook.service;

import androidx.annotation.Nullable;

import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.reflect.com.android.internal.telephony.HITelephonyRegistry;

public class ITelephonyRegistryHookHandle extends BaseBinderHookHandle {
    public ITelephonyRegistryHookHandle() {
        super("telephony.registry");
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        return HITelephonyRegistry.Stub.asInterface.call(getOriginBinder());
    }

    void addOnSubscriptionsChangedListener(MethodHandle methodHandle, String pkg, String featureId,
                                           Object callback) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, featureId, callback});
    }

    void addOnOpportunisticSubscriptionsChangedListener(MethodHandle methodHandle, String pkg, String featureId,
                                                        Object callback) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, featureId, callback});
    }

    void removeOnSubscriptionsChangedListener(MethodHandle methodHandle, String pkg,
                                              Object callback) {
        methodHandle.invokeOriginMethod(new Object[]{hostPkg, callback});
    }

    void listenWithEventList(MethodHandle methodHandle, boolean renounceFineLocationAccess,
                             boolean renounceCoarseLocationAccess, int subId, String pkg, String featureId,
                             Object callback, int[] events, boolean notifyNow) {
        methodHandle.invokeOriginMethod(new Object[]{renounceFineLocationAccess, renounceCoarseLocationAccess, subId, hostPkg, featureId, callback, events, notifyNow});
    }

    void addCarrierPrivilegesCallback(MethodHandle methodHandle,
                                      int phoneId, Object callback, String pkg, String featureId) {
        methodHandle.invokeOriginMethod(new Object[]{phoneId, callback, hostPkg, featureId});
    }

    void removeCarrierPrivilegesCallback(MethodHandle methodHandle, Object callback, String pkg) {
        methodHandle.invokeOriginMethod(new Object[]{callback, hostPkg});
    }

    void notifyCarrierServiceChanged(MethodHandle methodHandle, int phoneId, String packageName, int uid) {
        methodHandle.invokeOriginMethod(new Object[]{phoneId, hostPkg, uid});
    }

}
