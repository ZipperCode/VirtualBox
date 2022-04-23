#include "JniHook.h"
FileSystemHookHandle* JniHook::sFileSystemHookHandle = nullptr;
TestKotlinNativeHookHandle* JniHook::sTestKotlinNativeHookHandle = nullptr;
TestJavaNativeHookHandle* JniHook::sTestJavaNativeHookHandle = nullptr;

void JniHook::initHookEnv(JNIEnv *env)  {
    sTestKotlinNativeHookHandle = new TestKotlinNativeHookHandle(env);
    sTestJavaNativeHookHandle = new TestJavaNativeHookHandle(env);
    sFileSystemHookHandle = new FileSystemHookHandle(env);
}

void JniHook::enableNativeHook(JNIEnv *env) {
//    if (sTestKotlinNativeHookHandle != nullptr){
//        sTestKotlinNativeHookHandle->nativeHook(env);
//    }
//    if (sTestJavaNativeHookHandle != nullptr){
//        sTestJavaNativeHookHandle->nativeHook(env);
//    }
    if (sFileSystemHookHandle != nullptr){
        sFileSystemHookHandle->nativeHook(env);
    }
}
