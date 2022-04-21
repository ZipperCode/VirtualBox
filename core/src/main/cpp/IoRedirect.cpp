#include "IoRedirect.h"

bool IoRedirect::initRedirectPath(JNIEnv *env, jobjectArray originPaths, jobjectArray targetPaths) {
    jint originSize = env->GetArrayLength(originPaths);
    jint targetSize = env->GetArrayLength(targetPaths);
    if (originSize != targetSize){
        ALOGE("initRedirectPath >> error: originSize != targetSize : originSize = $d, targetSize = %d", originSize, targetSize)
        return false;
    }
    jboolean isCopy = false;
    for (int i = 0; i < originSize; ++i) {
        auto originJString = reinterpret_cast<jstring>(env->GetObjectArrayElement(originPaths, i));
        auto targetJString = reinterpret_cast<jstring>(env->GetObjectArrayElement(targetPaths, i));
        const char* originPathChar = env->GetStringUTFChars(originJString, &isCopy);
        const char* targetPathChar = env->GetStringUTFChars(targetJString, &isCopy);
        if (strstr(originPathChar, targetPathChar) && !strstr(originPathChar, "/virtual/")) {
            string originString(originPathChar);
            string targetString(targetPathChar);
            sRedirectMap.insert(pair<string,string>(originPathChar, targetString));
            ALOGD("===================================================================================")
            ALOGD(">> initRedirectPath >> 添加重定向规则 origin = %s", originPathChar)
            ALOGD(">> initRedirectPath >> 添加重定向规则 target = %s", originPathChar)
            ALOGD("===================================================================================")
        }else{
            ALOGE(">> initRedirectPath >> 不处理重定向 origin = %s， target = %s", originPathChar, targetPathChar)
            env->ReleaseStringUTFChars(originJString, originPathChar);
            env->ReleaseStringUTFChars(targetJString, targetPathChar);
        }
//        if (originString != targetString && originString.find("/virtual/") != string::npos){
//            sRedirectMap.insert(pair<string,string>(originPathChar, targetString));
//            ALOGE("initRedirectPath >> 添加重定向文件 origin = %s", originPathChar)
//        }
    }

    return true;
}

