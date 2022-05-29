#include "JniHook.h"
FileSystemHookHandle* JniHook::sFileSystemHookHandle = nullptr;

void JniHook::initHookEnv(JNIEnv *env)  {
    sFileSystemHookHandle = new FileSystemHookHandle(env);
}

void JniHook::enableNativeHook(JNIEnv *env) {
    if (sFileSystemHookHandle != nullptr){
        sFileSystemHookHandle->nativeHook(env);
    }
}
