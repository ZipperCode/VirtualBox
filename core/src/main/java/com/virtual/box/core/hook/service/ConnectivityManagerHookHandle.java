package com.virtual.box.core.hook.service;

import android.app.PendingIntent;
import android.content.Context;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.UserHandle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.core.VirtualBox;
import com.virtual.box.core.hook.core.MethodHandle;
import com.virtual.box.core.manager.ServiceManager;
import com.virtual.box.reflect.android.net.HIConnectivityManager;
import com.virtual.box.reflect.android.os.HServiceManager;

public class ConnectivityManagerHookHandle extends BaseBinderHookHandle {

    public ConnectivityManagerHookHandle() {
        super(Context.CONNECTIVITY_SERVICE);
    }

    @Nullable
    @Override
    protected Object getOriginObject() {
        IBinder binder = HServiceManager.getService.call(Context.CONNECTIVITY_SERVICE);
        return HIConnectivityManager.Stub.asInterface.call(binder);
    }

    NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(
            MethodHandle methodHandle,
            int userId, String callingPackageName, String callingAttributionTag) {
        return (NetworkCapabilities[]) methodHandle.invokeOriginMethod(new Object[]{
                userId, hostPkg, callingAttributionTag
        });
    }

    LinkProperties getRedactedLinkPropertiesForPackage(
            MethodHandle methodHandle, LinkProperties lp, int uid,
            String packageName, String callingAttributionTag) {
        return (LinkProperties) methodHandle.invokeOriginMethod(new Object[]{
                lp, uid, hostPkg, callingAttributionTag
        });
    }

    NetworkCapabilities getNetworkCapabilities(
            MethodHandle methodHandle, Network network, String callingPackageName,
            String callingAttributionTag) {
        return (NetworkCapabilities) methodHandle.invokeOriginMethod(new Object[]{
                network, hostPkg, callingAttributionTag
        });
    }

    NetworkCapabilities getRedactedNetworkCapabilitiesForPackage(
            MethodHandle methodHandle, NetworkCapabilities nc, int uid,
            String callingPackageName, String callingAttributionTag) {
        return (NetworkCapabilities) methodHandle.invokeOriginMethod(new Object[]{
                nc, uid, hostPkg, callingAttributionTag
        });
    }


    boolean requestRouteToHostAddress(MethodHandle methodHandle, int networkType, byte[] hostAddress,
                                      String callingPackageName, String callingAttributionTag) {
        return (boolean) methodHandle.invokeOriginMethod(new Object[]{
                networkType, hostAddress, hostPkg, callingAttributionTag
        });
    }


    NetworkRequest requestNetwork(
            MethodHandle methodHandle, int uid, NetworkCapabilities networkCapabilities, int reqType,
            Messenger messenger, int timeoutSec, IBinder binder, int legacy,
            int callbackFlags, String callingPackageName, String callingAttributionTag) {
        return (NetworkRequest) methodHandle.invokeOriginMethod(new Object[]{
                networkCapabilities, reqType, messenger, timeoutSec, binder, legacy, callbackFlags, hostPkg, callingAttributionTag
        });
    }

    NetworkRequest pendingRequestForNetwork(MethodHandle methodHandle, NetworkCapabilities networkCapabilities,
                                            PendingIntent operation, String callingPackageName, String callingAttributionTag) {
        return (NetworkRequest) methodHandle.invokeOriginMethod(new Object[]{
                networkCapabilities, operation, hostPkg, callingAttributionTag
        });
    }

    NetworkRequest listenForNetwork(MethodHandle methodHandle, NetworkCapabilities networkCapabilities,
                                    Messenger messenger, IBinder binder, int callbackFlags, String callingPackageName,
                                    String callingAttributionTag) {
        return (NetworkRequest) methodHandle.invokeOriginMethod(new Object[]{
                networkCapabilities, messenger, binder, callbackFlags, hostPkg, callingAttributionTag
        });
    }

    void pendingListenForNetwork(MethodHandle methodHandle, NetworkCapabilities networkCapabilities,
                                 PendingIntent operation, String callingPackageName,
                                 String callingAttributionTag) {
        methodHandle.invokeOriginMethod(new Object[]{
                networkCapabilities, operation, hostPkg, callingAttributionTag
        });
    }

    /**
     * @param callback IConnectivityDiagnosticsCallback
     */
    void registerConnectivityDiagnosticsCallback(MethodHandle methodHandle,
                                                 IInterface callback,
                                                 NetworkRequest request, String callingPackageName) {
        methodHandle.invokeOriginMethod(new Object[]{
                callback, request, hostPkg
        });
    }

}
