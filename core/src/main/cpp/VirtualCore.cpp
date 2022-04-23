//
// Created by Zipper on 2022/4/19.
//
#include <unistd.h>
#include "VirtualCore.h"
#include "hook/BaseHookHandle.h"

const static JNINativeMethod jniNativeMethods[] = {
        {"init",         "(IZ)I",                                     (int *) initVm},
        {"addIoRules",   "([Ljava/lang/String;[Ljava/lang/String;)V", (void *) addIoRules},
        {"nativeHook",   "()V",                                       (void *) nativeHook},
        {"redirectPath", "(Ljava/lang/String;)Ljava/lang/String;",    (jstring *) redirectPath}
};
const static VirtualCore *gVmCore;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *unused) {
    ALOGD(">> VirtualCore JNI OnLoad");
    gVmCore = new VirtualCore(vm);
    return registerNativeMethod(vm);
}

int initVm(JNIEnv *env, jclass clazz, jint android_level, jboolean debug) {
    if (gVmCore == nullptr) {
        ALOGE(">> VirtualCore#init fail: gVmCore == null")
        return JNI_ERR;
    }
    ALOGD(">> VirtualCore init");
    // initDebugLog(debug);
    gVmCore->initVirtualEnv(env, android_level, debug);
    return JNI_OK;
}

void addIoRules(JNIEnv *env, jclass clazz, jobjectArray originPaths, jobjectArray targetPaths) {
    if (gVmCore == nullptr) {
        ALOGE(">> VirtualCore#addIoRules fail: gVmCore == null")
        return;
    }
    ALOGD(">> VirtualCore#addIoRules");
    IoRedirect::initRedirectRule(env, originPaths, targetPaths);
}

void nativeHook(JNIEnv *env, jclass clazz) {
    JniHook::enableNativeHook(env);
}

jstring redirectPath(JNIEnv *env, jclass clazz, jstring originPath) {
    return IoRedirect::handleRedirectPath(env, originPath);
}

VirtualCore::VirtualCore(JavaVM *vm) {
    this->mTid = syscall(SYS_gettid);
    ALOGD(">> VirtualCore >> 获取当前线程id = %ld", mTid)
}

void VirtualCore::initVirtualEnv(JNIEnv *env, int android_level, bool debug) const {
    ArtMethodHandle::initArtMethod(env, this->androidLevel);
    IoRedirect::initJavaIoEnv(env);
    JniHook::initHookEnv(env);
}

int registerNativeMethod(JavaVM *jvm) {
    JNIEnv *env;
    jvm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    jclass virtualCoreClass = env->FindClass(VM_CORE_CLASS);
    if (virtualCoreClass == nullptr) {
        ALOGE(">> VirtualCore fail: not found %s", VM_CORE_CLASS)
        env->ExceptionClear();
        return JNI_EVERSION;
    }
    if (env->RegisterNatives(virtualCoreClass, jniNativeMethods,
                             sizeof(jniNativeMethods) / sizeof(jniNativeMethods[0])) != JNI_OK) {
        ALOGE(">> VirtualCore >> Register VirtualCore Native Method error")
        return JNI_EVERSION;
    }
    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_virtual_box_core_hook_core_VmCore_replaceMethod(JNIEnv *env, jclass clazz, jlong replace_method, jlong target_method) {
    if (gVmCore == nullptr){
        return -1;
    }
    memcpy(reinterpret_cast<void *const>(target_method),
           reinterpret_cast<const void *>(replace_method),
           ArtMethodHandle::sArtMethodSize * sizeof(uint32_t));
    return 1;
}