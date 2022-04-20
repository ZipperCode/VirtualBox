
#ifndef VIRTUAL_BOX_JNI_HOOK_
#define VIRTUAL_BOX_JNI_HOOK_

#include <jni.h>
#include "utils/log.h"

static struct {
    int android_level;
} JniHookEnv;

/**
 * hook jni方法
 * @param env               env环境
 * @param class_name        要hook的class
 * @param method_name       hook的方法名
 * @param sign              hook的方法签名 (II)V
 * @param new_fun           代理函数的指针
 * @param orig_fun          源函数定义，函数定义的指针地址，传入时不需要有实际值
 * @param is_static         是否是静态函数
 * @return JNI_TRUE JNI_FALSE
 */
int handle_hook_native_func(
        JNIEnv *env, const char *class_name,
        const char *method_name, const char *sign,
        void *new_fun, void **orig_fun, bool is_static
);

int handle_hook_java_func(
        JNIEnv *env, const char *class_name,
        const char *method_name, const char *sign,
        void *new_fun, void **orig_fun, bool is_static
        );


#endif