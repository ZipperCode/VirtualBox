package com.virtual.box.reflect.android.app;

import com.virtual.box.reflect.MirrorReflection;

public class HActivityTaskManager {

    public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityTaskManager");

    public static MirrorReflection.FieldWrapper<Object> IActivityTaskManagerSingleton = REF.field("IActivityTaskManagerSingleton");
    public static MirrorReflection.StaticMethodWrapper<Object> getService = REF.staticMethod("getService");

}
