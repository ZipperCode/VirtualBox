package com.virtual.box.reflect.android.app;


import android.app.ActivityThread;
import android.app.Application;
import android.app.LoadedApk;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.virtual.box.reflect.MirrorReflection;

public class HService {
    public static final MirrorReflection REF = MirrorReflection.on(Service.class);

    public static MirrorReflection.MethodWrapper<Void> attach = REF.method("attach",
        Context.class, ActivityThread.class, String.class, IBinder.class,
            Application.class, Object.class);

    public static MirrorReflection.MethodWrapper<Void> detachAndCleanUp = REF.method("detachAndCleanUp");
}
