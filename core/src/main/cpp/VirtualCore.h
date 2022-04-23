//
// Created by Zipper on 2022/4/19.
//

#ifndef VIRTUALBOX_VIRTUALCORE_H
#define VIRTUALBOX_VIRTUALCORE_H
#include <jni.h>
#include <sys/syscall.h>

#include "utils/log.h"
#include "utils/JnIEnvRef.h"
#include "hook/JniHook.h"
#include "ArtMethodHandle.h"
#include "IoRedirect.h"

#define VM_CORE_CLASS "com/virtual/box/core/hook/core/VmCore"
/**
 * JNI 初始化入口
 * @param android_level
 * @return
 */
int initVm(JNIEnv *env, jclass clazz, jint android_level, jboolean debug);

void addIoRules(JNIEnv *env, jclass clazz, jobjectArray originPaths, jobjectArray targetPaths);

void nativeHook(JNIEnv* env, jclass clazz);
/**
 * Java调用重定向的文件路径
 * data/data/com/a/b => data/data/[host]/data/data/com/a/b
 * @param originPath        需要定向的路径
 * @return                  定向的路径
 */
jstring redirectPath(JNIEnv*env, jclass clazz, jstring originPath);

int registerNativeMethod(JavaVM *jvm);

class VirtualCore{

private:
    int androidLevel = 0;
    JavaVM *mJavaVm = nullptr;
    long mTid = 0;
    bool mDebug = false;
public:
    VirtualCore(JavaVM* vm);

public:

    void initVirtualEnv(JNIEnv* env, int android_level, bool debug) const;
public:
    int getAndroidLevel() const{
        return androidLevel;
    }

    bool hasDebug() const{
        return mDebug;
    }

    __attribute__((always_inline)) JniEnvRef* getJniEnv() const{
        return new JniEnvRef(mJavaVm);
    }
};
#endif //VIRTUALBOX_VIRTUALCORE_H
