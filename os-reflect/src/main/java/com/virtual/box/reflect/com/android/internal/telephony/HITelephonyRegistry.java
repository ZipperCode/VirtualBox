package com.virtual.box.reflect.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HITelephonyRegistry {

	public static class Stub {
		public static final MirrorReflection REF = MirrorReflection.on("com.android.internal.telephony.ITelephonyRegistry$Stub");
		public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
	}
}
