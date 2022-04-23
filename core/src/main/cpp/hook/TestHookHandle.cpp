#include "TestHookHandle.h"

HOOK_JNI(void, kotlinStaticRegister, JNIEnv *env, jobject obj) {
    ALOGE(">> HOOK_TEST >> hook > kotlinStaticRegister")
    orig_kotlinStaticRegister(env, obj);
}

HOOK_JNI(void, kotlinDynamicRegister, JNIEnv *env, jobject obj) {
    ALOGE(">> HOOK_TEST >> hook > kotlinDynamicRegister")
    orig_kotlinDynamicRegister(env, obj);
}

HOOK_JNI(void, staticRegJavaArtMethod, JNIEnv *env, jobject obj) {
    ALOGE(">> HOOK_TEST >> hook > staticRegJavaArtMethod")
    orig_staticRegJavaArtMethod(env, obj);
}

HOOK_JNI(void, javaDynamicRegister, JNIEnv *env, jobject obj) {
    ALOGE(">> HOOK_TEST >> hook > javaDynamicRegister")
    orig_javaDynamicRegister(env, obj);
}

void TestKotlinNativeHookHandle::nativeHook(JNIEnv *env) {
    handleHook(env,"kotlinDynamicRegister", "()V",
                            (void *) new_kotlinDynamicRegister,
                            (void **) (&orig_kotlinDynamicRegister),
                            true
    );
}

void TestJavaNativeHookHandle::nativeHook(JNIEnv *env) {
    handleHook(env,"javaDynamicRegister", "()V",
               (void *) new_kotlinDynamicRegister,
               (void **) (&orig_kotlinDynamicRegister),
               true
    );
}
