#ifndef VIRTUAL_BOX_HOOK_HANDLE_H
#define VIRTUAL_BOX_HOOK_HANDLE_H

#include "BaseHookHandle.h"

#define JAVA_UNIX_FILE_SYSTEM_CLASS_NAME "java/io/UnixFileSystem"

class FileSystemHookHandle : public BaseHookHandle {
public:
    explicit FileSystemHookHandle(JNIEnv *env) : BaseHookHandle(env, JAVA_UNIX_FILE_SYSTEM_CLASS_NAME) {}

    void nativeHook(JNIEnv *env) override;
};
#endif