package com.virtual.box.reflect.libcore.io;

import com.virtual.box.reflect.MirrorReflection;

public class HLibcore {
    public static final String NAME = "libcore.io.Libcore";
    public static final MirrorReflection REF = MirrorReflection.on(NAME);

    public static MirrorReflection.FieldWrapper<Object> os = REF.field("os");
}
