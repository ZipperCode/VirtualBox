#ifndef VIRTUAL_BOX_BASE_HOOK_HANDLE_H
#define VIRTUAL_BOX_BASE_HOOK_HANDLE_H

/**
 * JNI Hook 定义 将指定方法替换为代理方法
 *      HOOK_JNI(void*, fun, int, int)
 * =>   void* fun(int, int);
 * =>   void* new_fun(int, int)
 */
#define HOOK_JNI(ret, func, ...) \
  ret (*orig_##func)(__VA_ARGS__); \
  ret new_##func(__VA_ARGS__)

#include <jni.h>
#include "utils/log.h"
#include "utils/modifiers.h"
#include "IoRedirect.h"
#include "ArtMethodHandle.h"

class BaseHookHandle{
protected:
    const char* mClassName;
    BaseHookHandle(JNIEnv *env, const char *name): mClassName(name){
        jclass hookClass = env->FindClass(mClassName);
        if (hookClass == nullptr){
            env->ExceptionClear();
            return;
        }
        // 正常情况下so库的加载和某个类一起，所以可以起到初始化so库作用，
        // 至于加载地点不一致的地方，后续再看（系统库一般都很早会加载好）
        jobject initLibLoad = env->AllocObject(hookClass);
        if (initLibLoad != nullptr){
            env->DeleteLocalRef(initLibLoad);
        }
    }

public:

    virtual void nativeHook(JNIEnv*env) = 0;
protected:
    /**
     * hook jni方法
     * 通过获取ArtMethod方法的指针使用动态注册的方式
     * 将新函数注册为原函数
     *
     * @param env               env环境
     * @param method_name       hook的方法名
     * @param sign              hook的方法签名 (II)V
     * @param new_fun           代理函数的指针
     * @param orig_fun          源函数定义，函数定义的指针地址，传入时不需要有实际值
     * @param is_static         是否是静态函数
     * @return JNI_TRUE JNI_FALSE
     */
    int handleHook(
            JNIEnv *env, const char *method_name, const char *sign,
            void *new_fun, void **orig_fun, bool is_static
            ){
        jclass hookClass = env->FindClass(mClassName);
        if (!hookClass) {
            ALOGE("hookNativeFunc >> not found class fail: %s %s", mClassName, method_name);
            env->ExceptionClear();
            return JNI_FALSE;
        }
        jmethodID method;
        if (is_static) {
            method = env->GetStaticMethodID(hookClass, method_name, sign);
        } else {
            method = env->GetMethodID(hookClass, method_name, sign);
        }
        if (!method) {
            env->ExceptionClear();
            ALOGD("hookNativeFunc >> get method id fail: %s %s", mClassName, method_name);
            return JNI_FALSE;
        }
        // 将c函数转换为jniNative函数
        JNINativeMethod jniNativeMethod[] = {
                {method_name, sign, (void *) new_fun},
        };
        // 检查是否是native方法
        auto pArtMethod = reinterpret_cast<uint32_t *>(ArtMethodHandle::getArtMethodPtr(env, hookClass, method));
        ALOGD(">> flags = %s" , prettyJavaAccessFlags(ArtMethodHandle::getAccessFlags(pArtMethod)).c_str())
        if (!ArtMethodHandle::checkNativeMethod(pArtMethod)) {
            ALOGE("hookNativeFunc >> check flags error. class：%s, method：%s", mClassName, method_name);
            return JNI_FALSE;
        }
        // Android 8.0 ,8.1 必须清除 FastNative 标志才能注册成功,所以如果原来包含 FastNative 标志还得恢复,
        // 否者调用原方法可能会出现问题
        ArtMethodHandle::clearFastNativeFlag(pArtMethod);
        auto pNativeOffset = pArtMethod + ArtMethodHandle::getArtMethodNativeOffset();
        ALOGD(">> pNativeOffset Address     = %p", pNativeOffset)
        ALOGD(">> pNativeOffset Value       = %x", (size_t)(*pNativeOffset))
        // 如果JNI方法没有注册，这边拿到的地址将会是art_jni_dlsym_lookup_stub
        *orig_fun = reinterpret_cast<void*>(*pNativeOffset);
        // 拿到art函数指针 赋值到 orig_fun中，完成native方法的hook
        // 将java native方法注册为自定义的方法
        if (env->RegisterNatives(hookClass, jniNativeMethod, 1) < 0) {
            ALOGE("hookNativeFunc >> jni hook error. class：%s, method：%s", mClassName, method_name);
            return JNI_FALSE;
        }
        // 添加fastNative优化
        if (ArtMethodHandle::getAndroidLevel() >= __ANDROID_API_O__){
            ArtMethodHandle::addAccessFlags(pArtMethod, kAccFastNative);
        }
        return JNI_TRUE;
    }

public:
    // Returns a human-readable version of the Java part of the access flags, e.g., "private static "
    // (note the trailing whitespace).
    static std::string prettyJavaAccessFlags(uint32_t access_flags) {
        std::string result;
        if ((access_flags & kAccPublic) != 0) {
            result += "public ";
        }
        if ((access_flags & kAccProtected) != 0) {
            result += "protected ";
        }
        if ((access_flags & kAccPrivate) != 0) {
            result += "private ";
        }
        if ((access_flags & kAccFinal) != 0) {
            result += "final ";
        }
        if ((access_flags & kAccStatic) != 0) {
            result += "static ";
        }
        if ((access_flags & kAccAbstract) != 0) {
            result += "abstract ";
        }
        if ((access_flags & kAccInterface) != 0) {
            result += "interface ";
        }
        if ((access_flags & kAccTransient) != 0) {
            result += "transient ";
        }
        if ((access_flags & kAccVolatile) != 0) {
            result += "volatile ";
        }
        if ((access_flags & kAccSynchronized) != 0) {
            result += "synchronized ";
        }
        if ((access_flags & kAccNative) != 0){
            result += "native";
        }
        return result;
    }
};

#endif