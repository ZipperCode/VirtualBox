#ifndef VIRTUAL_BOX_TEST_HOOK_HANDLE_H
#define VIRTUAL_BOX_TEST_HOOK_HANDLE_H

#include "hook/BaseHookHandle.h"
#define JAVA_KOTLIN_TEST_CLASS_NAME "com/virtual/test/NativeLib"
#define JAVA_JAVA_TEST_CLASS_NAME "com/virtual/test/JavaNativeLib"

class TestKotlinNativeHookHandle : public BaseHookHandle {
public:
    explicit TestKotlinNativeHookHandle(JNIEnv *env) : BaseHookHandle(env, JAVA_KOTLIN_TEST_CLASS_NAME) {}

    void nativeHook(JNIEnv *env) override;
};

class TestJavaNativeHookHandle : public BaseHookHandle {
public:
    explicit TestJavaNativeHookHandle(JNIEnv *env) : BaseHookHandle(env, JAVA_JAVA_TEST_CLASS_NAME) {}

    void nativeHook(JNIEnv *env) override;
};

#endif