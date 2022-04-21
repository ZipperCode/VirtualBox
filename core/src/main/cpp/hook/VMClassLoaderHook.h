
#ifndef VIRTUAL_BOX_VM_CLASSLOADER_HOOK_H
#define VIRTUAL_BOX_VM_CLASSLOADER_HOOK_H

#include <cstring>
#include "BaseHook.h"

class VMClassLoaderHook : public BaseHook {
public:
    static void hideXposed();
    static void init(JNIEnv *env);
};


#endif
