#ifndef VIRTUAL_BOX_IO_REDIRECT_H
#define VIRTUAL_BOX_IO_REDIRECT_H

#include <jni.h>
#include <iostream>
#include <map>
#include <string>

#include "utils/log.h"

using namespace std;

class IoRedirect{

public:
    /**
     * 重定向路径
     */
    static map<string, string> sRedirectMap;

public:
    static bool initRedirectPath(JNIEnv *env, jobjectArray originPaths, jobjectArray targetPaths);
};
#endif