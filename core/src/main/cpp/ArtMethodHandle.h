#ifndef VIRTUAL_BOX_ART_METHOD_H
#define VIRTUAL_BOX_ART_METHOD_H

#include <jni.h>
#include "utils/log.h"
#include "utils/modifiers.h"

#define JAVA_ART_METHOD "com/virtual/box/core/hook/method/ArtMethod"
#define OFFSET_METHOD_1 "nativeOffset"
#define OFFSET_METHOD_2 "nativeOffset2"
#define OFFSET_METHOD_SIGN "()V"

class ArtMethodHandle{
private:
    static JNIEnv* sJniEnv;
    static int sAndroidLevel;
public:
    static bool sInitArtMethodOffsetStatus;
    static uint32_t sArtMethodAccFlagOffset;
    static uint32_t sArtMethodNativeOffset;
    static uint32_t sArtMethodSize;
    static uint32_t sArtMethodDexCodeItemOffset;
    static uint32_t sArtMethodDexCodeItemOffsetValue;
    static uint32_t sArtMethodDexMethodIndexOffset;
    static uint32_t sArtMethodDexMethodIndexValue;

public:
    /**
     * 初始化ArtMethod方
     * @param env
     * @param android_level
     * @return
     */
    static int initArtMethod(JNIEnv *env, int android_level);

    static long calculateArtMethodFlag(void* pArtMethod);

    static void* getArtMethodPtr(JNIEnv *env, jclass clazz, jmethodID methodId);

    static uint32_t getAccessFlags(void * pArtMethod);

    static bool setAccessFlags(void* pArtMethod, uint32_t flags);
    /**
     * 给普通方法添加native标识
     * @param pArtMethod
     */
    static void addNativeAccessFlag(void * pArtMethod);

    static void addAccessFlags(void* pArtMethod, uint32_t flag);

    static bool checkNativeMethod(void* pArtMethod);

    static bool clearFastNativeFlag(void *art_method);

    static bool clearAccessFlag(void *art_method, uint32_t flag);
private:
    static int registerArtMethod(JNIEnv *env);

public:
    static uint32_t getJavaOffsetMethodAccFlag(JNIEnv *env);
    static int getAndroidLevel(){
        return sAndroidLevel;
    }

    static uint32_t getArtMethodAccFlagOffset(){
        return sArtMethodAccFlagOffset;
    }

    static uint32_t getArtMethodNativeOffset(){
        return sArtMethodNativeOffset;
    }

    static uint32_t getAerMethodSize(){
        return sArtMethodSize;
    }

    static uint32_t getOffsetMethodFlag(){
        uint32_t flags = 0x0;
        flags = flags | kAccPublic;
        flags = flags | kAccStatic;
        flags = flags | kAccNative;
        flags = flags | kAccFinal;
        if (sAndroidLevel >= __ANDROID_API_Q__) {
            flags = flags | kAccPublicApi;
        }

        ALOGD("getOffsetMethodFlag: flag = %x", flags)
        if (sJniEnv != nullptr){
            auto reflectAccFlag = getJavaOffsetMethodAccFlag(sJniEnv);
            ALOGD("getOffsetMethodFlag: modifier = %x", reflectAccFlag)
        }
        return flags;
    }

    static void toString();

    static void printArtMethod(const char *name, void* pArtMethod);
};
#endif