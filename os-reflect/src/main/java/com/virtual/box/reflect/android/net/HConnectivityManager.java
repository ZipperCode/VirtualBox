package com.virtual.box.reflect.android.net;

import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

/**
 * @author zhangzhipeng
 * @date 2022/3/29
 **/
public class HConnectivityManager {

    public static final MirrorReflection REF = MirrorReflection.on("android.net.ConnectivityManager");


    public static MirrorReflection.StaticMethodWrapper<ConnectivityManager> getInstance = REF.staticMethod("getInstance");

    public static MirrorReflection.FieldWrapper<ConnectivityManager> sInstance = REF.field("sInstance");

    public static MirrorReflection.FieldWrapper<IInterface> mService = REF.field("mService");

    public static MirrorReflection.MethodWrapper<IInterface> getNetworkManagementService = REF.method("getNetworkManagementService");


}

