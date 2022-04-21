#include "ProcessHook.h"

/**
 * 函数翻译：
 * void sendSignal(JNIEnv *env, jobject obj, jint pid, jint signal);
 * void new_sendSignal(JNIEnv *env, jobject obj, jint pid, jint signal){
 *    ALOGE("hooked sendSignal");
 * }
 */
HOOK_JNI(void, sendSignal, JNIEnv *env, jobject obj, jint pid, jint signal) {
    ALOGE("hooked sendSignal");
}
/**
 * 函数翻译：
 * void sendSignalQuiet(JNIEnv *env, jobject obj, jint pid, jint signal);
 * void new_sendSignalQuiet(JNIEnv *env, jobject obj, jint pid, jint signal){
 *    ALOGE("hooked sendSignalQuiet");
 * }
 */
HOOK_JNI(void, sendSignalQuiet, JNIEnv *env, jobject obj, jint pid, jint signal) {
    ALOGE("hooked sendSignalQuiet");
}
/**
 * 函数翻译：
 * jint killProcessGroup(JNIEnv *env, jobject obj, jint uid, jint pid);
 * jint new_killProcessGroup(JNIEnv *env, jobject obj, jint uid, jint pid){
 *    ALOGE("hooked killProcessGroup");
 * }
 */
HOOK_JNI(jint, killProcessGroup, JNIEnv *env, jobject obj, jint uid, jint pid) {
    ALOGE("hooked killProcessGroup");
    return 0;
}

void ProcessHook::init(JNIEnv *env) {
    const char *className = "android/os/Process";
    handle_hook_native_func(env, className, "sendSignal", "(II)V",
                        (void *) new_sendSignal,
                        (void **) (&orig_sendSignal), true);

    handle_hook_native_func(env, className, "sendSignalQuiet", "(II)V",
                        (void *) new_sendSignalQuiet,
                        (void **) (&orig_sendSignalQuiet), true);

    handle_hook_native_func(env, className, "killProcessGroup", "(II)I",
                        (void *) new_killProcessGroup,
                        (void **) (&orig_killProcessGroup), true);
}
