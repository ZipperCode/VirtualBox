#include "JniHook.h"
#include "ArtMethodHandle.h"

int handle_hook_native_func(
        JNIEnv *env, const char *class_name,
        const char *method_name, const char *sign,
        void *new_fun, void **orig_fun, bool is_static
){
    jclass clazz = env->FindClass(class_name);
    if (!clazz) {
        ALOGE("hookNativeFunc >> not found class fail: %s %s", class_name, method_name);
        env->ExceptionClear();
        return JNI_FALSE;
    }

    jmethodID method;
    if (is_static) {
        method = env->GetStaticMethodID(clazz, method_name, sign);
    } else {
        method = env->GetMethodID(clazz, method_name, sign);
    }

    if (!method) {
        env->ExceptionClear();
        ALOGD("hookNativeFunc >> get method id fail: %s %s", class_name, method_name);
        return JNI_FALSE;
    }
    // 将c函数转换为jniNative函数
    JNINativeMethod gMethods[] = {
            {method_name, sign, (void *) new_fun},
    };

    // 检查是否是native方法
    auto pArtMethod = reinterpret_cast<uintptr_t *>(ArtMethodHandle::GetArtMethodPtr(env, clazz, method));
    if (!ArtMethodHandle::CheckNativeMethod(pArtMethod)) {
        ALOGE("hookNativeFunc >> check flags error. class：%s, method：%s", class_name, method_name);
        return false;
    }
    // 拿到art函数指针 赋值到 orig_fun中，完成native方法的hook
    *orig_fun = reinterpret_cast<void *>(pArtMethod[ArtMethodHandle::getArtMethodNativeOffset()]);
    // 将java native方法注册为自定义的方法
    if (env->RegisterNatives(clazz, gMethods, 1) < 0) {
        ALOGE("hookNativeFunc >> jni hook error. class：%s, method：%s", class_name, method_name);
        return JNI_FALSE;
    }
    // 添加fastNative优化
    if (ArtMethodHandle::getAndroidLevel() >= __ANDROID_API_O__){
        ArtMethodHandle::AddAccessFlags(pArtMethod, kAccFastNative);
    }
    return JNI_TRUE;
}
int handle_hook_java_func(
        JNIEnv *env, const char *class_name,
        const char *method_name, const char *sign,
        void *new_fun, void **orig_fun, bool is_static
){
    jclass clazz = env->FindClass(class_name);
    if (!clazz) {
        ALOGE("hookNativeFunc >> not found class fail: %s %s", class_name, method_name);
        env->ExceptionClear();
        return JNI_FALSE;
    }

    jmethodID method;
    if (is_static) {
        method = env->GetStaticMethodID(clazz, method_name, sign);
    } else {
        method = env->GetMethodID(clazz, method_name, sign);
    }

    if (!method) {
        env->ExceptionClear();
        ALOGD("hookNativeFunc >> get method id fail: %s %s", class_name, method_name);
        return JNI_FALSE;
    }

    // 检查是否是native方法
    auto pArtMethod = reinterpret_cast<uintptr_t *>(ArtMethodHandle::GetArtMethodPtr(env, clazz, method));
    if (ArtMethodHandle::CheckNativeMethod(pArtMethod)) {
        ALOGE("hookNativeFunc >> check flags error: has native func class：%s, method：%s", class_name, method_name);
        return false;
    }
    // 将java方法设置成native方法
    ArtMethodHandle::AddNativeAccessFlag(pArtMethod);

    // 将c函数转换为jniNative函数
    JNINativeMethod gMethods[] = {
            {method_name, sign, (void *) new_fun},
    };


}

