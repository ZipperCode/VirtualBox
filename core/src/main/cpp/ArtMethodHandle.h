#ifndef VIRTUAL_BOX_ART_METHOD_H
#define VIRTUAL_BOX_ART_METHOD_H

#include <jni.h>
#include "utils/log.h"

#define JAVA_ART_METHOD "com/virtual/box/core/hook/method/ArtMethod"
#define OFFSET_METHOD_1 "nativeOffset"
#define OFFSET_METHOD_2 "nativeOffset2"
#define OFFSET_METHOD_SIGN "()V"


static constexpr uint32_t kAccPublic =       0x0001;  // class, field, method, ic
static constexpr uint32_t kAccPrivate =      0x0002;  // field, method, ic
static constexpr uint32_t kAccProtected =    0x0004;  // field, method, ic
static constexpr uint32_t kAccStatic =       0x0008;  // field, method, ic
static constexpr uint32_t kAccFinal =        0x0010;  // class, field, method, ic
static constexpr uint32_t kAccSynchronized = 0x0020;  // method (only allowed on natives)
static constexpr uint32_t kAccSuper =        0x0020;  // class (not used in dex)
static constexpr uint32_t kAccVolatile =     0x0040;  // field
static constexpr uint32_t kAccBridge =       0x0040;  // method (1.5)
static constexpr uint32_t kAccTransient =    0x0080;  // field
static constexpr uint32_t kAccVarargs =      0x0080;  // method (1.5)
static constexpr uint32_t kAccNative =       0x0100;  // method
static constexpr uint32_t kAccInterface =    0x0200;  // class, ic
static constexpr uint32_t kAccAbstract =     0x0400;  // class, method, ic
static constexpr uint32_t kAccStrict =       0x0800;  // method
static constexpr uint32_t kAccSynthetic =    0x1000;  // class, field, method, ic
static constexpr uint32_t kAccAnnotation =   0x2000;  // class, ic (1.5)
static constexpr uint32_t kAccEnum =         0x4000;  // class, field, ic (1.5)

static constexpr uint32_t kAccPublicApi =             0x10000000;  // field, method
static constexpr uint32_t kAccCorePlatformApi =       0x20000000;  // field, method

// Native method flags are set when linking the methods based on the presence of the
// @dalvik.annotation.optimization.{Fast,Critical}Native annotations with build visibility.
// Reuse the values of kAccSkipAccessChecks and kAccMiranda which are not used for native methods.
static constexpr uint32_t kAccFastNative =            0x00080000;  // method (runtime; native only)
static constexpr uint32_t kAccCriticalNative =        0x00200000;  // method (runtime; native only)


class ArtMethodHandle{
private:
    static JNIEnv* sJniEnv;
    static int sAndroidLevel;
public:
    static bool sInitArtMethodOffsetStatus;
    static size_t sArtMethodAccFlagOffset;
    static size_t sArtMethodNativeOffset;
    static size_t sArtMethodSize;
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
    static int InitArtMethod(JNIEnv *env, int android_level);

    static long CalculateArtMethodFlag(uint32_t * pArtMethod);

    static void* GetArtMethodPtr(JNIEnv *env, jclass clazz, jmethodID methodId);

    static uint32_t GetAccessFlags(const uint32_t * pArtMethod);

    static bool SetAccessFlags(uint32_t* pArtMethod, uint32_t flags);
    /**
     * 给普通方法添加native标识
     * @param pArtMethod
     */
    static void AddNativeAccessFlag(uint32_t * pArtMethod);

    static void AddAccessFlags(uintptr_t* pArtMethod, uint32_t flag);

    static bool CheckNativeMethod(uintptr_t* pArtMethod);

    static bool ClearFastNativeFlag(uintptr_t *art_method);

    static bool ClearAccessFlag(uintptr_t *art_method, uint32_t flag);
private:
    static int registerArtMethod(JNIEnv *env);

    static uint32_t getJavaOffsetMethodAccFlag(JNIEnv *env);
public:
    static int getAndroidLevel(){
        return sAndroidLevel;
    }

    static long getArtMethodAccFlagOffset(){
        return sArtMethodAccFlagOffset;
    }

    static long getArtMethodNativeOffset(){
        return sArtMethodNativeOffset;
    }

    static long getAerMethodSize(){
        return sArtMethodSize;
    }

    static uint32_t GetOffsetMethodFlag(){
        uint32_t flags = 0x0;
        flags = flags | kAccPublic;
        flags = flags | kAccStatic;
        flags = flags | kAccNative;
        flags = flags | kAccFinal;
        if (sAndroidLevel >= __ANDROID_API_Q__) {
            flags = flags | kAccPublicApi;
        }

        ALOGD("GetOffsetMethodFlag: flag = %x", flags)
        if (sJniEnv != nullptr){
            auto reflectAccFlag = getJavaOffsetMethodAccFlag(sJniEnv);
            ALOGD("GetOffsetMethodFlag: modifier = %x", reflectAccFlag)
        }
        return flags;
    }

    static void toString();

    static void printArtMethod(uintptr_t* pArtMethod);
};
static ArtMethodHandle gArtMethod;
#endif