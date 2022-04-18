package com.virtual.box.reflect.android.net;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

/**
 * @author zhangzhipeng
 * @date 2022/3/29
 **/
public class HIConnectivityManager {

    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.net.IConnectivityManager$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
