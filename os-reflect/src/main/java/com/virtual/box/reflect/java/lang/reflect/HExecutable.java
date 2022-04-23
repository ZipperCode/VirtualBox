package com.virtual.box.reflect.java.lang.reflect;

import com.virtual.box.reflect.MirrorReflection;

public class HExecutable {
    public static final String NAME = "java.lang.reflect.Executable";
    public static final MirrorReflection REF = MirrorReflection.on(NAME);

    public static MirrorReflection.FieldWrapper<Long> artMethod = REF.field("artMethod");
}
