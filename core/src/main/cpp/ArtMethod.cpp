#include "ArtMethod.h"

__attribute__((section (".mytext")))  JNICALL void native_offset
        (JNIEnv *env, jclass obj) {
}

__attribute__((section (".mytext")))  JNICALL void native_offset2
        (JNIEnv *env, jclass obj) {
}

void initArtMethod(JNIEnv *env, int android_level){

}

int ArtMethod::InitArtMethod(JNIEnv *env, int android_level) {
    auto res = registerArtMethod(env);
    if (res){
        ALOGE("register java art method fail");
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

long ArtMethod::CalculateArtMethodFlag(long *pArtMethod) {

    return 0;
}

int ArtMethod::registerArtMethod(JNIEnv *env) {
    jclass clazz = env->FindClass(JAVA_ART_METHOD);
    JNINativeMethod gNativeArtMethods[] = {
            {"nativeOffset",  "()V",        (void *) native_offset},
            {"nativeOffset2", "()V",        (void *) native_offset2},
    };
    if (env->RegisterNatives(clazz, gNativeArtMethods, sizeof(gNativeArtMethods) / sizeof(gNativeArtMethods[0])) < 0) {
        ALOGE("jni register error.");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}




