package com.virtual.box.reflect.android.app.servertransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import com.virtual.box.reflect.MirrorReflection;

public class HLaunchActivityItem {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.servertransaction.LaunchActivityItem");

    public static MirrorReflection.FieldWrapper<Intent> mIntent = REF.field("mIntent");
    public static MirrorReflection.FieldWrapper<ActivityInfo> mInfo = REF.field("mInfo");
}
