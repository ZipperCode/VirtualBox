#ifndef VIRTUAL_BOX_IO_REDIRECT_H
#define VIRTUAL_BOX_IO_REDIRECT_H

#include <jni.h>
#include <iostream>
#include <map>
#include <string>

#include "utils/log.h"

#define JAVA_FILE_CLASS "java/io/File"
#define JAVA_FILE_GET_ABSOLUTE_PATH_METHOD "getAbsolutePath"
#define JAVA_FILE_GET_ABSOLUTE_PATH_METHOD_SIGN "()Ljava/lang/String;"
#define JAVA_FILE_CONSTRUCTOR_PATH_METHOD_SIGN "(Ljava/lang/String;)V"

using namespace std;

class IoRedirect{
private:
    /**
     * 是否初始化
     */
    static bool sHasInit;
public:
    /**
     * 重定向路径
     */
    static map<const char*, const char *> sRedirectMap;

    static jclass sFileClass;

    static jmethodID sFileGetAbsolutePathMethod;

    static jmethodID sFileConstructorMethod;

public:
    static bool initRedirectRule(JNIEnv *env, jobjectArray originPaths, jobjectArray targetPaths);
    /**
     * 处理路径的重定向
     * @param path
     * @return
     */
    static const char* handleRedirectPath(const char* path);

    static jstring handleRedirectPath(JNIEnv *env, jstring filePath);

    static jobject handleRedirectPath(JNIEnv *env, jobject fileObj);
    static bool initJavaIoEnv(JNIEnv *env);
};
#endif