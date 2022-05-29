#ifndef VIRTUAL_BOX_LINUX_HOOK_HANDLE_H
#define VIRTUAL_BOX_LINUX_HOOK_HANDLE_H

#include "BaseHookHandle.h"

#define JAVA_LINUX_CLASS_NAME "libcore/io/Linux"

class LinuxHookHandle : public BaseHookHandle {
public:
    explicit LinuxHookHandle(JNIEnv *env) : BaseHookHandle(env, JAVA_LINUX_CLASS_NAME) {}

    void nativeHook(JNIEnv *env) override;
};
#endif