#ifndef VIRTUAL_BOX_BASE_HOOK_H
#define VIRTUAL_BOX_BASE_HOOK_H

#include <jni.h>
#include <sys/types.h>
#import "JniHook.h"
#import "utils/log.h"

/**
 * JNI Hook 定义 将指定方法替换为代理方法
 *      HOOK_JNI(void*, fun, int, int)
 * =>   void* fun(int, int);
 * =>   void* new_fun(int, int)
 */
#define HOOK_JNI(ret, func, ...) \
  ret (*orig_##func)(__VA_ARGS__); \
  ret new_##func(__VA_ARGS__)

class BaseHook {
public:
    static void init(JNIEnv *env);
};


#endif
