#include <jni.h>
#include <string>
#include <android/log.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_virtual_test_NativeLib_kotlinStaticRegister(JNIEnv *env, jclass clazz) {
    __android_log_print(ANDROID_LOG_DEBUG, "HOOK_TEST", "Kotlin 静态注册的函数");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_virtual_test_JavaNativeLib_javaStaticRegister(JNIEnv *env, jclass clazz) {
    __android_log_print(ANDROID_LOG_DEBUG, "HOOK_TEST", "Java 静态注册的函数");
}

void testKotlinDynamicNative(){
    __android_log_print(ANDROID_LOG_DEBUG, "HOOK_TEST", "Kotlin 动态注册的函数");
}

void testJavaDynamicRegister(){
    __android_log_print(ANDROID_LOG_DEBUG, "HOOK_TEST", "Java 动态注册的函数");
}


const static JNINativeMethod jniNativeMethods1[] = {
        {"kotlinDynamicRegister","()V", (int *) testKotlinDynamicNative}
};

const static JNINativeMethod jniNativeMethods2[] = {
        {"javaDynamicRegister","()V", (int *) testJavaDynamicRegister}
};

JNIEXPORT int JNI_OnLoad(JavaVM *vm, void *unused) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) > 0){
        jclass kotlinClass = env->FindClass("com/virtual/test/NativeLib");
        env->RegisterNatives(kotlinClass, jniNativeMethods1, 1);

        jclass javaClass = env->FindClass("com/virtual/test/JavaNativeLib");
        env->RegisterNatives(javaClass, jniNativeMethods2, 1);
    }
    return JNI_VERSION_1_6;
}

