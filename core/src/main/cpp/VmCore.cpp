//
// Created by Zipper on 2022/4/19.
//
#include "VmCore.h"
const static JNINativeMethod jniNativeMethods[] = {
        {"init","(I)I", (int *) initVm}
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *unused) {
    ALOGD("Virtual JNI OnLoad");
    JNIEnv *env;
    VmEnv.vm = vm;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK){
        ALOGD("get jni evn error");
        return JNI_EVERSION;
    }
    VmEnv.env = env;
    VmEnv.vmCoreClass = env->FindClass(VM_CORE_CLASS);
    return JNI_VERSION_1_6;
}

int initVm(int android_level){
    ALOGD("VmCore init");
    VmEnv.android_level = android_level;
    registerVmNativeMethod();
    return 0;
}

int registerVmNativeMethod() {
    if (VmEnv.env->RegisterNatives(VmEnv.vmCoreClass, jniNativeMethods,
                             sizeof(jniNativeMethods) / sizeof(jniNativeMethods[0])) != JNI_OK){
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

int registerOffsetNativeMethod(){
    return 0;
}
