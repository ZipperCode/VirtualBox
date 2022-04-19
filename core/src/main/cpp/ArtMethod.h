#ifndef VIRTUAL_BOX_ART_METHOD_H
#define VIRTUAL_BOX_ART_METHOD_H

#include <jni.h>
#include "util/log.h"

#define JAVA_ART_METHOD "com/virtual/box/core/hook/method/ArtMethod"
#define __ANDROID_API_R__ 30
#define __ANDROID_API_Q__ 29
#define __ANDROID_API_P__ 28



class ArtMethod{
private:
    static JNIEnv* jniEnv;
public:
    static long sInitArtMethodOffset;
    static long sArtMethodAccFlagOffset;
    static long sArtMethodNativeOffset;
    static int sArtMethodSize;

public:
    static int InitArtMethod(JNIEnv *env, int android_level);

    static long CalculateArtMethodFlag(long * pArtMethod);

private:
    static int registerArtMethod(JNIEnv *env);

public:
    static long getArtMethodAccFlagOffset(){
        return sArtMethodAccFlagOffset;
    }

    static long getArtMethodNativeOffset(){
        return sArtMethodNativeOffset;
    }

    static long getAerMethodSize(){
        return sArtMethodSize;
    }
};
static ArtMethod gArtMethod;
#endif