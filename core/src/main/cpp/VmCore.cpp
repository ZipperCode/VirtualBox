//
// Created by Zipper on 2022/4/19.
//
#include "VmCore.h"

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *unused) {
    VmCore::vm = vm;
    if (vm->GetEnv(reinterpret_cast<void **>(&VmCore::env), JNI_VERSION_1_6) != JNI_OK){
        ALOGD("get jni evn error");
        return JNI_EVERSION;
    }
    VmCore::init();
    return 0;
}


int VmCore::init() {
    ALOGD("VmCore init");
    vmCoreClass = env->FindClass(VM_CORE_CLASS);
    if (vmCoreClass == nullptr){
        return JNI_FALSE;
    }
    if (env->RegisterNatives(vmCoreClass, jniNativeMethods, sizeof(jniNativeMethods) / sizeof(jniNativeMethods[0])) != JNI_OK){
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

int VmCore::registerNativeMethod() {


    return 0;
}
