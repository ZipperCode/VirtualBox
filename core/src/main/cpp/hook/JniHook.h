#ifndef VIRTUAL_BOX_JNI_HOOK_H
#define VIRTUAL_BOX_JNI_HOOK_H

#include <jni.h>
#include "FileSystemHookHandle.h"
#include "LinuxHookHandle.h"
#include "TestHookHandle.h"

class JniHook {
private:
    static FileSystemHookHandle *sFileSystemHookHandle;
    static LinuxHookHandle *sLinuxHookHandle;
    static TestKotlinNativeHookHandle *sTestKotlinNativeHookHandle;
    static TestJavaNativeHookHandle *sTestJavaNativeHookHandle;
public:

    static void initHookEnv(JNIEnv *env);

    static void enableNativeHook(JNIEnv*env);
};


#endif