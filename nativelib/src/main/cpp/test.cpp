#include <jni.h>
#include <string>
#include <android/log.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_virtual_test_NativeLib_kotlinStaticRegister(JNIEnv *env, jclass clazz) {
    __android_log_print(ANDROID_LOG_ERROR, "HOOK_TEST", "Kotlin 静态注册的函数");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_virtual_test_JavaNativeLib_javaStaticRegister(JNIEnv *env, jclass clazz) {
    __android_log_print(ANDROID_LOG_ERROR, "HOOK_TEST", "Java 静态注册的函数");
}

void testKotlinDynamicNative(JNIEnv *env, jclass clazz){
    __android_log_print(ANDROID_LOG_ERROR, "HOOK_TEST", "Kotlin 动态注册的函数");
}

void testJavaDynamicRegister(JNIEnv *env, jclass clazz){
    __android_log_print(ANDROID_LOG_ERROR, "HOOK_TEST", "Java 动态注册的函数");
}


const static JNINativeMethod jniNativeMethods1[] = {
        {"kotlinDynamicRegister","()V", (void *) testKotlinDynamicNative}
};

const static JNINativeMethod jniNativeMethods2[] = {
        {"javaDynamicRegister","()V", (void *) testJavaDynamicRegister}
};

JNIEXPORT int JNI_OnLoad(JavaVM *vm, void *unused) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK){
        return JNI_EVERSION;
    }
    jclass kotlinClass = env->FindClass("com/virtual/test/NativeLib");
    env->RegisterNatives(kotlinClass, jniNativeMethods1, 1);

    jclass javaClass = env->FindClass("com/virtual/test/JavaNativeLib");
    env->RegisterNatives(javaClass, jniNativeMethods2, 1);
    return JNI_VERSION_1_6;
}

