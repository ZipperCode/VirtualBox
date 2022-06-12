//
// Created by Zipper on 2022/4/19.
//
#include <unistd.h>
#include <iostream>
#include <string>
#include "VirtualCore.h"
#include "hook/BaseHookHandle.h"

const static JNINativeMethod jniNativeMethods[] = {
        {"init",         "(IZ)I",                                     (int *) initVm},
        {"addIoRules",   "([Ljava/lang/String;[Ljava/lang/String;)V", (void *) addIoRules},
        {"nativeHook",   "()V",                                       (void *) nativeHook},
        {"redirectPath", "(Ljava/lang/String;)Ljava/lang/String;",    (jstring *) redirectPath}
};
static VirtualCore *gVmCore;

bool hasInit = false;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *unused) {
    gVmCore = new VirtualCore(vm);
    return registerNativeMethod(vm);
}

int initVm(JNIEnv *env, jclass clazz, jint android_level, jboolean debug) {
    if (gVmCore == nullptr) {
        ALOGE(">> VirtualCore#init fail: gVmCore == null")
        return JNI_ERR;
    }
    if (hasInit){
        ALOGE(">> VirtualCore#init fail: has init")
        return JNI_OK;
    }
    ALOGD(">> VirtualCore init");
    // initDebugLog(debug);
    gVmCore->initVirtualEnv(env, android_level, debug);
    hasInit = true;
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

void VirtualCore::initVirtualEnv(JNIEnv *env, int android_level, bool debug) {
    this->androidLevel = android_level;
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
    ALOGE(">> VirtualCore Native 函数注册成功 %s", VM_CORE_CLASS)
    return JNI_VERSION_1_6;
}

class ArtMethodContentCopy {
public:
    char *originArtMethod;
    char *targetArtMethod;

    ArtMethodContentCopy(char &origin_art_method, char &target_art_method) {
        originArtMethod = &origin_art_method;
        targetArtMethod = &target_art_method;
    }

    ArtMethodContentCopy(size_t artMethodUInt8Size, jlong origin_method, jlong target_method){
        auto *originMethod = reinterpret_cast<uint8_t *>(origin_method);
        auto *targetMethod = reinterpret_cast<uint8_t *>(target_method);

        originArtMethod = new char[artMethodUInt8Size]();
        targetArtMethod = new char[artMethodUInt8Size]();
        // 保存原artMethod内容
        memcpy(originArtMethod, originMethod, artMethodUInt8Size);
        // 保存原ArtMethod方法内容的内存开始指针
        memcpy(targetArtMethod, targetMethod, artMethodUInt8Size);
    }

    ~ArtMethodContentCopy() {
        if (originArtMethod != nullptr) {
            delete originArtMethod;
        }
        if (targetArtMethod != nullptr) {
            delete targetArtMethod;
        }
    }
};

void HexDump(const char*name, uint8_t &start, unsigned int len){
    auto *lineStart = (uintptr_t *) &start;
    auto *linePos = &start;
    int i = 0;
    int lineSize = 32;

    char buff[128];
    ALOGE(">> ========================================= %s#Start", name)
    while (i < len){
        std::string lineStr;
        int writePos = sprintf(buff,"%p :", lineStart);
        lineStr.append(buff, 0, writePos);
        if (i + lineSize >= len){
            lineSize = len - i;
        }

        for (int j = 0; j < lineSize; j++){
            if (j % 4 == 0){
                lineStr.append("  ");
            }
            writePos = sprintf(buff,"%02X ", linePos[j]);
            lineStr.append(buff, 0, writePos);
        }
        ALOGE(">> %s",lineStr.c_str())
        linePos += lineSize;
        i += lineSize;
    }
    ALOGE("<< ========================================= %s#End ", name)
}

extern "C"
JNIEXPORT void JNICALL
Java_com_virtual_box_core_hook_core_VmCore_switchRedirect(JNIEnv *env, jclass clazz, jboolean enable) {

}

extern "C" JNIEXPORT jlong JNICALL
Java_com_virtual_box_core_hook_core_VmCore_replaceMethod(
        JNIEnv *env, jclass clazz, jlong replace_method, jlong target_method) {
    if (gVmCore == nullptr) {
        return -1;
    }

    if (ArtMethodHandle::sArtMethodSize == 0) {
        ALOGE(">> VirtualCore >> sArtMethodSize 大小 == 0 不处理替换")
        return -1;
    }

    const size_t artMethodUInt8Size = ArtMethodHandle::sArtMethodSize * sizeof(uint32_t);
    auto *originMethod = reinterpret_cast<uint8_t *>(replace_method);
    auto *targetMethod = reinterpret_cast<uint8_t *>(target_method);
    auto *copyOriginArtMethod = new ArtMethodContentCopy(artMethodUInt8Size, replace_method, target_method);
//    HexDump("Hook方法指针前的值",*targetMethod, artMethodUInt8Size);
    memcpy(targetMethod, originMethod, artMethodUInt8Size);
//    HexDump("Hook方法指针后的值",*targetMethod, artMethodUInt8Size);
    return reinterpret_cast<jlong>(copyOriginArtMethod);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_virtual_box_core_hook_core_VmCore_restoreMethod(
        JNIEnv *env, jclass clazz, jlong method_content_ptr, jlong target_method_ptr) {
    auto *copyOriginArtMethod = reinterpret_cast<ArtMethodContentCopy *>(method_content_ptr);
    if (copyOriginArtMethod == nullptr) {
        ALOGE(">> VirtualCore >> 恢复被hook的方法失败: 方法拷贝不存在")
        return -1;
    }
    if (ArtMethodHandle::sArtMethodSize == 0) {
        ALOGE(">> VirtualCore >> 恢复被hook的方法失败: sArtMethodSize 大小 == 0 不处理替换")
        return -1;
    }
    const size_t artMethodUInt8Size = ArtMethodHandle::sArtMethodSize * sizeof(uint32_t);
    auto *originMethod = reinterpret_cast<uint8_t *>(copyOriginArtMethod->targetArtMethod);
    auto *targetMethod = reinterpret_cast<uint8_t *>(target_method_ptr);
    memcpy(targetMethod, originMethod, artMethodUInt8Size);
//    HexDump("恢复Hook方法指针后的值",*targetMethod, artMethodUInt8Size);
    delete copyOriginArtMethod;
    return 1;
}


extern "C"
JNIEXPORT jint JNICALL
Java_me_weishu_reflection_Reflection_unsealNative(JNIEnv *env, jclass type, jint targetSdkVersion) {
    return unseal(env, targetSdkVersion);
//    return  0;
}