#include "JniHook.h"
FileSystemHookHandle* JniHook::sFileSystemHookHandle = nullptr;
LinuxHookHandle* JniHook::sLinuxHookHandle = nullptr;

void JniHook::initHookEnv(JNIEnv *env)  {
    sFileSystemHookHandle = new FileSystemHookHandle(env);
    sLinuxHookHandle = new LinuxHookHandle(env);
}

void JniHook::enableNativeHook(JNIEnv *env) {
    if (sFileSystemHookHandle != nullptr){
        sFileSystemHookHandle->nativeHook(env);
    }

    if (sLinuxHookHandle != nullptr){
        sLinuxHookHandle->nativeHook(env);
    }
}
