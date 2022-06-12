#include "LinuxHookHandle.h"
/**
 *  public native boolean access(String path, int mode) throws ErrnoException;
 */
HOOK_JNI(jboolean, access, JNIEnv *env, jobject obj, jstring path, jint mode) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#access path = %s, mode = %d", pathStr, mode)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_access(env, obj, redirect, mode);
}
/**
 *  public native void chmod(String path, int mode) throws ErrnoException;
 */
HOOK_JNI(void, chmod, JNIEnv *env, jobject obj, jstring path, jint mode) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#chmod path = %s, mode = %d", pathStr, mode)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_chmod(env, obj, redirect, mode);
}
/**
 * public native void chown(String path, int uid, int gid) throws ErrnoException;
 */
HOOK_JNI(void, chown, JNIEnv *env, jobject obj, jstring path, jint uid, jint gid) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#chown path = %s, uid = %d, gid = %d", pathStr, uid, gid)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_chown(env, obj, redirect, uid, gid);
}

/**
 * public native void execv(String filename, String[] argv) throws ErrnoException;
 */
HOOK_JNI(void, execv, JNIEnv *env, jobject obj, jstring filename, jobjectArray argv) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(filename, JNI_FALSE);
        ALOGE(">> Linux#execv filename = %s", pathStr)
        env->ReleaseStringUTFChars(filename, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, filename);
    orig_execv(env, obj, redirect, argv);
}

/**
 * public native void execve(String filename, String[] argv, String[] envp) throws ErrnoException;
 */
HOOK_JNI(void, execve, JNIEnv *env, jobject obj, jstring filename, jobjectArray argv, jobjectArray envp) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(filename, JNI_FALSE);
        ALOGE(">> Linux#execve filename = %s", pathStr)
        env->ReleaseStringUTFChars(filename, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, filename);
    orig_execve(env, obj, redirect, argv, envp);
}

/**
 * public native byte[] getxattr(String path, String name) throws ErrnoException;
 */
HOOK_JNI(jbyteArray, getxattr, JNIEnv *env, jobject obj, jstring path, jstring name) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        const char *nameStr = env->GetStringUTFChars(name, JNI_FALSE);
        ALOGE(">> Linux#getxattr path = %s, name = %s", pathStr, nameStr)
        env->ReleaseStringUTFChars(path, pathStr);
        env->ReleaseStringUTFChars(name, nameStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_getxattr(env, obj, redirect, name);
}

/**
 * public native void lchown(String path, int uid, int gid) throws ErrnoException;
 */
HOOK_JNI(void, lchown, JNIEnv *env, jobject obj, jstring path, jint uid, jint gid) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#lchown path = %s, uid = %d, gid = %d", pathStr, uid, gid)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_lchown(env, obj, redirect, uid, gid);
}

/**
 * public native void link(String oldPath, String newPath) throws ErrnoException;
 */
HOOK_JNI(void, link, JNIEnv *env, jobject obj, jstring oldPath, jstring newPath) {
    if (DEBUG_LOG) {
        const char *oldPathStr = env->GetStringUTFChars(oldPath, JNI_FALSE);
        const char *newPathStr = env->GetStringUTFChars(newPath, JNI_FALSE);
        ALOGE(">> Linux#link oldPath = %s, newPath = %s", oldPathStr, newPathStr)
        env->ReleaseStringUTFChars(oldPath, oldPathStr);
        env->ReleaseStringUTFChars(newPath, newPathStr);
    }
    jstring redirectOldPath = IoRedirect::handleRedirectPath(env, oldPath);
    jstring redirectNewPath = IoRedirect::handleRedirectPath(env, newPath);
    orig_link(env, obj, redirectOldPath, redirectNewPath);
}

/**
 * public native String[] listxattr(String path) throws ErrnoException;
 */
HOOK_JNI(jobjectArray, listxattr, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#listxattr path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_listxattr(env, obj, redirect);
}

/**
 * public native StructStat lstat(String path) throws ErrnoException;
 */
HOOK_JNI(jobject, lstat, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#lstat path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_lstat(env, obj, redirect);
}

/**
 *  public native void mkdir(String path, int mode) throws ErrnoException;
 */
HOOK_JNI(void, mkdir, JNIEnv *env, jobject obj, jstring path, jint mode) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#mkdir path = %s, mode = %d", pathStr, mode)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_mkdir(env, obj, redirect, mode);
}

/**
 *  public native void mkfifo(String path, int mode) throws ErrnoException;
 */
HOOK_JNI(void, mkfifo, JNIEnv *env, jobject obj, jstring path, jint mode) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#mkfifo path = %s, mode = %d", pathStr, mode)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_mkfifo(env, obj, redirect, mode);
}

/**
 *  public native FileDescriptor open(String path, int flags, int mode) throws ErrnoException;
 */
HOOK_JNI(jobject, open, JNIEnv *env, jobject obj, jstring path, jint flag, jint mode) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#open path = %s, flag = %d, mode = %d", pathStr, flag, mode)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_open(env, obj, redirect, flag, mode);
}


/**
 *  public native String readlink(String path) throws ErrnoException;
 */
HOOK_JNI(jstring, readlink, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#readlink path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_readlink(env, obj, redirect);
}

/**
 *  public native String realpath(String path) throws ErrnoException;
 */
HOOK_JNI(jstring, realpath, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#mkfifo path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_realpath(env, obj, redirect);
}

/**
 *  public native void remove(String path) throws ErrnoException;
 */
HOOK_JNI(void, remove, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#remove path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_remove(env, obj, redirect);
}

/**
 *  public native void removexattr(String path, String name) throws ErrnoException;
 */
HOOK_JNI(void, removexattr, JNIEnv *env, jobject obj, jstring path, jstring name) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#removexattr path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_removexattr(env, obj, redirect, name);
}

/**
 * public native void rename(String oldPath, String newPath) throws ErrnoException;
 */
HOOK_JNI(void, rename, JNIEnv *env, jobject obj, jstring oldPath, jstring newPath) {
    if (DEBUG_LOG) {
        const char *oldPathStr = env->GetStringUTFChars(oldPath, JNI_FALSE);
        const char *newPathStr = env->GetStringUTFChars(newPath, JNI_FALSE);
        ALOGE(">> Linux#rename oldPath = %s, newPath = %s", oldPathStr, newPathStr)
        env->ReleaseStringUTFChars(oldPath, oldPathStr);
        env->ReleaseStringUTFChars(newPath, newPathStr);
    }
    jstring redirectOldPath = IoRedirect::handleRedirectPath(env, oldPath);
    jstring redirectNewPath = IoRedirect::handleRedirectPath(env, newPath);
    orig_rename(env, obj, redirectOldPath, redirectNewPath);
}

/**
 * public native void setxattr(String path, String name, byte[] value, int flags) throws ErrnoException;
 */
HOOK_JNI(void, setxattr, JNIEnv *env, jobject obj, jstring path, jstring name, jbyteArray value, jint flags) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#setxattr path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    orig_setxattr(env, obj, redirect, name, value, flags);
}

/**
 * public native StructStat stat(String path) throws ErrnoException;
 */
HOOK_JNI(jobject, stat, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#stat path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_stat(env, obj, redirect);
}

/**
 * public native StructStatVfs statvfs(String path) throws ErrnoException;
 */
HOOK_JNI(jobject, statvfs, JNIEnv *env, jobject obj, jstring path) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(path, JNI_FALSE);
        ALOGE(">> Linux#statvfs path = %s", pathStr)
        env->ReleaseStringUTFChars(path, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, path);
    return orig_statvfs(env, obj, redirect);
}

/**
 * public native void symlink(String oldPath, String newPath) throws ErrnoException;
 */
HOOK_JNI(void, symlink, JNIEnv *env, jobject obj, jstring oldPath, jstring newPath) {
    if (DEBUG_LOG) {
        const char *oldPathStr = env->GetStringUTFChars(oldPath, JNI_FALSE);
        const char *newPathStr = env->GetStringUTFChars(newPath, JNI_FALSE);
        ALOGE(">> Linux#symlink oldPath = %s, newPath = %s", oldPathStr, newPathStr)
        env->ReleaseStringUTFChars(oldPath, oldPathStr);
        env->ReleaseStringUTFChars(newPath, newPathStr);
    }
    jstring redirectOldPath = IoRedirect::handleRedirectPath(env, oldPath);
    jstring redirectNewPath = IoRedirect::handleRedirectPath(env, newPath);
    orig_symlink(env, obj, redirectOldPath, redirectNewPath);
}

/**
 * public native void unlink(String pathname) throws ErrnoException;
 */
HOOK_JNI(void, unlink, JNIEnv *env, jobject obj, jstring pathname) {
    if (DEBUG_LOG) {
        const char *pathStr = env->GetStringUTFChars(pathname, JNI_FALSE);
        ALOGE(">> Linux#unlink pathname = %s", pathStr)
        env->ReleaseStringUTFChars(pathname, pathStr);
    }
    jstring redirect = IoRedirect::handleRedirectPath(env, pathname);
    orig_unlink(env, obj, redirect);
}

void LinuxHookHandle::nativeHook(JNIEnv *env) {
    ALOGE(">> LinuxHookHandle::nativeHook start")
    handleHook(env, "access", "(Ljava/lang/String;I)Z", HOOK_PTR(access));
    handleHook(env, "chmod", "(Ljava/lang/String;I)V",  HOOK_PTR(chmod));
    handleHook(env, "chown", "(Ljava/lang/String;II)V", HOOK_PTR(chown));
    handleHook(env, "execv", "(Ljava/lang/String;[Ljava/lang/String;)V", HOOK_PTR(execv));
    handleHook(env, "execve", "(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V", HOOK_PTR(execve));
    handleHook(env, "getxattr", "(Ljava/lang/String;Ljava/lang/String;)[B", HOOK_PTR(getxattr));
    handleHook(env, "lchown", "(Ljava/lang/String;II)V", HOOK_PTR(lchown));
    handleHook(env, "link", "(Ljava/lang/String;Ljava/lang/String;)V", HOOK_PTR(link));
    handleHook(env, "listxattr", "(Ljava/lang/String;)[Ljava/lang/String;", HOOK_PTR(listxattr));
    handleHook(env, "lstat", "(Ljava/lang/String;)Landroid/system/StructStat;", HOOK_PTR(lstat));
    handleHook(env, "mkdir", "(Ljava/lang/String;I)V", HOOK_PTR(mkdir));
    handleHook(env, "mkfifo", "(Ljava/lang/String;I)V", HOOK_PTR(mkfifo));
    handleHook(env, "open", "(Ljava/lang/String;II)Ljava/io/FileDescriptor;", HOOK_PTR(open));
    handleHook(env, "readlink", "(Ljava/lang/String;)Ljava/lang/String;", HOOK_PTR(readlink));
    handleHook(env, "realpath", "(Ljava/lang/String;)Ljava/lang/String;", HOOK_PTR(realpath));
    handleHook(env, "remove", "(Ljava/lang/String;)V", HOOK_PTR(remove));
    handleHook(env, "removexattr", "(Ljava/lang/String;Ljava/lang/String;)V", HOOK_PTR(removexattr));
    handleHook(env, "rename", "(Ljava/lang/String;Ljava/lang/String;)V", HOOK_PTR(rename));
    handleHook(env, "stat", "(Ljava/lang/String;)Landroid/system/StructStat;", HOOK_PTR(stat));
    handleHook(env, "statvfs", "(Ljava/lang/String;)Landroid/system/StructStatVfs;", HOOK_PTR(statvfs));
    handleHook(env, "symlink", "(Ljava/lang/String;Ljava/lang/String;)V", HOOK_PTR(symlink));
    handleHook(env, "unlink", "(Ljava/lang/String;)V", HOOK_PTR(unlink));
    ALOGE(">> LinuxHookHandle::nativeHook end")
}