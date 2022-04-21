#ifndef VIRTUAL_BOX_PROCESS_HOOK_H
#define VIRTUAL_BOX_PROCESS_HOOK_H

#include "BaseHook.h"

class ProcessHook : public BaseHook {
public:
    static void init(JNIEnv *env);
};


#endif
