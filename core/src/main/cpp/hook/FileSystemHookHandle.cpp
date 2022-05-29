#include "FileSystemHookHandle.h"
/*
 * 此方法用户创建文件
 * 如果文件存在返回false，创建成功返回true
 *
 * Class:     java_io_UnixFileSystem
 * Method:    createFileExclusively0
 * Signature: (Ljava/lang/String;)Z;
 */
HOOK_JNI(jboolean, createFileExclusively0, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGD(">> Hook 创建文件：path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_createFileExclusively0(env, obj, redirect);
}

/*
 * 用于列出指定目录下的所有文件和目录
 *
 * Class:     java_io_UnixFileSystem
 * Method:    list0
 * Signature: (Ljava/io/File;)[Ljava/lang/String;
 */
HOOK_JNI(jobjectArray, list0, JNIEnv *env, jobject obj, jobject file) {
    ALOGE(">> UnixFileSystem#list0 > obj = %s, file = %s", obj, file)
    jobject redirect = IoRedirect::handleRedirectPath(env, file);
    return orig_list0(env, obj, redirect);
}

/*
 * 该方法用来创建目录
 *
 * Class:     java_io_UnixFileSystem
 * Method:    createDirectory0
 * Signature: (Ljava/io/File;)Z
 */
HOOK_JNI(jboolean, createDirectory0, JNIEnv *env, jobject obj, __attribute__((unused)) jobject filePath) {
    jobject redirect = IoRedirect::handleRedirectPath(env, filePath);
    return orig_createDirectory0(env, obj, redirect);
}

/*
 * 该方法用来设置文件或目录的最后修改时间
 *
 * Class:     java_io_UnixFileSystem
 * Method:    setLastModifiedTime0
 * Signature: (Ljava/io/File;J)Z
 */
HOOK_JNI(jboolean, setLastModifiedTime0, JNIEnv *env, jobject obj, jobject file, jobject time) {
    jobject redirect = IoRedirect::handleRedirectPath(env, file);
    return orig_setLastModifiedTime0(env, obj, redirect, time);
}

/*
 * 该方法用来获取文件或目录的最后修改时间
 *
 * Class:     java_io_UnixFileSystem
 * Method:    getLastModifiedTime0
 * Signature: (Ljava/io/File;)J
 */
HOOK_JNI(jlong, getLastModifiedTime0, JNIEnv *env, jobject obj, jobject filePath) {
    jobject redirect = IoRedirect::handleRedirectPath(env, filePath);
    return orig_getLastModifiedTime0(env, obj, redirect);
}

/*
 * 用于将指定文件设置成只读
 *
 * Class:     java_io_UnixFileSystem
 * Method:    setReadOnly0
 * Signature: (Ljava/io/File;)Z
 */
HOOK_JNI(jboolean, setReadOnly0, JNIEnv *env, jobject obj, jobject file) {
    jobject redirect = IoRedirect::handleRedirectPath(env, file);
    return orig_setReadOnly0(env, obj, redirect);
}

/*
 * Class:     java_io_UnixFileSystem
 * Method:    canonicalize0
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
HOOK_JNI(jstring, canonicalize0, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_canonicalize0(env, obj, redirect);
}

/*
 * Class:     java_io_UnixFileSystem
 * Method:    getBooleanAttributes0
 * Signature: (Ljava/lang/String;)I
 */
HOOK_JNI(jint, getBooleanAttributes0, JNIEnv *env, jobject obj, jstring abspath) {
    jstring redirect = IoRedirect::handleRedirectPath(env, abspath);
    return orig_getBooleanAttributes0(env, obj, redirect);
}

/*
 * Class:     java_io_UnixFileSystem
 * Method:    setPermission0
 * Signature: (Ljava/io/File;IZZ)Z
 */
HOOK_JNI(jboolean, setPermission0, JNIEnv *env, jobject obj, jobject file, jint access,
         jboolean enable, jboolean owneronly) {
    jobject redirect = IoRedirect::handleRedirectPath(env, file);
    return orig_setPermission0(env, obj, redirect, access, enable, owneronly);
}

/*
 * Class:     java_io_UnixFileSystem
 * Method:    getSpace0
 * Signature: (Ljava/io/File;I)J
 */
HOOK_JNI(jboolean, getSpace0, JNIEnv *env, jobject obj, jobject file, jint t) {
    jobject redirect = IoRedirect::handleRedirectPath(env, file);
    return orig_getSpace0(env, obj, redirect, t);
}
/*
 * Class:     java_io_UnixFileSystem
 * Method:    getSpace0
 * Signature: (Ljava/io/File;I)J
 */
HOOK_JNI(jboolean, checkAccess, JNIEnv *env, jobject obj, jboolean access) {
    ALOGD("UnixFileSystem >> file = %s, access = %u", obj, access);
    return orig_checkAccess(env, obj, access);
}

void FileSystemHookHandle::nativeHook(JNIEnv *env) {
//    handleHook(env, "canonicalize0", "(Ljava/lang/String;)Ljava/lang/String;",
//               (void *) new_canonicalize0, (void **) (&orig_canonicalize0), false);
//    handleHook(env, "getLastModifiedTime0", "(Ljava/io/File;)J",
//               (void *) new_getLastModifiedTime0, (void **) (&orig_getLastModifiedTime0),
//               false);
//    handleHook(env, "setPermission0", "(Ljava/io/File;IZZ)Z",
//               (void *) new_setPermission0, (void **) (&orig_setPermission0), false);
//    handleHook(env, "createFileExclusively0", "(Ljava/lang/String;)Z",
//               (void *) new_createFileExclusively0,
//               (void **) (&orig_createFileExclusively0), false);
//    handleHook(env, "list0", "(Ljava/io/File;)[Ljava/lang/String;",
//               (void *) new_list0, (void **) (&orig_list0), false);
//    handleHook(env, "createDirectory0", "(Ljava/io/File;)Z",
//               (void *) new_createDirectory0, (void **) (&orig_createDirectory0), false);
//    // 设置最后修改时间
//    handleHook(env, "setLastModifiedTime0", "(Ljava/io/File;J)Z",
//               (void *) new_setLastModifiedTime0, (void **) (&orig_setLastModifiedTime0),
//               false);
//    // 设置只读
//    handleHook(env, "setReadOnly0", "(Ljava/io/File;)Z",
//               (void *) new_setReadOnly0, (void **) (&orig_setReadOnly0), false);
//    // 获取文件空间大小
//    handleHook(env, "getSpace0", "(Ljava/io/File;I)J",
//               (void *) new_getSpace0, (void **) (&orig_getSpace0), false);
//    // 检查文件是否存在，
//    handleHook(env, "checkAccess", "(Ljava/io/File;I)Z",
//               (void *) new_checkAccess, (void **) (&orig_checkAccess), false);
}

