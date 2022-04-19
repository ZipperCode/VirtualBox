#include "reflect.h"

void *get_static_method_ptr(
        JNIEnv *env,
        const char *cls_name,
        const char *method_name,
        const char *method_sign
) {
    jclass clazz = env->FindClass(cls_name);
    return env->GetStaticMethodID(clazz, method_name, method_sign);
}