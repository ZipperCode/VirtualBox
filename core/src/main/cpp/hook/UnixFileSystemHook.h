#ifndef VIRTUAL_BOX_UNIX_FILESYSTEM_HOOK_H
#define VIRTUAL_BOX_UNIX_FILESYSTEM_HOOK_H

#include "BaseHook.h"

class UnixFileSystemHook : public BaseHook {
public:
    static void init(JNIEnv *env);
};


#endif
