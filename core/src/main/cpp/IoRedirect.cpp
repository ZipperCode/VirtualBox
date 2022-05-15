#include "IoRedirect.h"

bool IoRedirect::sHasInit = false;
map<const char*, const char*> IoRedirect::sRedirectMap;
jclass IoRedirect::sFileClass = nullptr;
jmethodID IoRedirect::sFileGetAbsolutePathMethod = nullptr;
jmethodID IoRedirect::sFileConstructorMethod = nullptr;

bool IoRedirect::initRedirectRule(JNIEnv *env, jobjectArray originPaths, jobjectArray targetPaths) {
    if (!sHasInit){
        ALOGE("initRedirectRule >> error: 初始化失败")
        return false;
    }

    jint originSize = env->GetArrayLength(originPaths);
    jint targetSize = env->GetArrayLength(targetPaths);
    if (originSize != targetSize){
        ALOGE("initRedirectRule >> error: originSize != targetSize : originSize = %d, targetSize = %d", originSize, targetSize)
        return false;
    }
    jboolean isCopy = false;
    for (int i = 0; i < originSize; ++i) {
        auto originJString = reinterpret_cast<jstring>(env->GetObjectArrayElement(originPaths, i));
        auto targetJString = reinterpret_cast<jstring>(env->GetObjectArrayElement(targetPaths, i));
        const char* originPathChar = env->GetStringUTFChars(originJString, &isCopy);
        const char* targetPathChar = env->GetStringUTFChars(targetJString, &isCopy);
        if (strstr(originPathChar, targetPathChar) && !strstr(originPathChar, "/virtual/")) {
            sRedirectMap.insert(pair<const char*,const char*>(originPathChar, originPathChar));
            ALOGD("===============================================================================================================")
            ALOGD(">> initRedirectRule >> 添加重定向规则 origin = %s", originPathChar)
            ALOGD(">> initRedirectRule >> 添加重定向规则 target = %s", originPathChar)
            ALOGD("===============================================================================================================")
        }else{
            ALOGE(">> initRedirectRule >> 不处理重定向 origin = %s， target = %s", originPathChar, targetPathChar)
            env->ReleaseStringUTFChars(originJString, originPathChar);
            env->ReleaseStringUTFChars(targetJString, targetPathChar);
        }
//        if (originString != targetString && originString.find("/virtual/") != string::npos){
//            sRedirectMap.insert(pair<string,string>(originPathChar, targetString));
//            ALOGE("initRedirectRule >> 添加重定向文件 origin = %s", originPathChar)
//        }
    }
    sHasInit = true;
    return true;
}

const char *IoRedirect::handleRedirectPath(const char *path) {
    if (!sHasInit){
        return path;
    }
    if (path == nullptr || strlen(path) == 0){
        ALOGE("handleRedirectPath >> 参数 path == null")
        return path;
    }
    if (sRedirectMap.empty()){
        ALOGE("handleRedirectPath >> rule empty")
        return path;
    }
    const char *redirectOriginRule = nullptr;
    const char *redirectTargetRule = nullptr;
    for(auto & iter : sRedirectMap){
        redirectOriginRule = iter.first;
        if (redirectOriginRule != nullptr && strstr(path, redirectOriginRule)){
            // 包含子串或者相等
            redirectTargetRule = iter.second;
            break;
        }
    }
    if (redirectOriginRule == nullptr || redirectTargetRule == nullptr){
        ALOGE("handleRedirectPath >> 未找到匹配的规则，path = %s", path)
        return path;
    }

    auto *pathString = new string(path);
    size_t keyInPathIndex = pathString->find(redirectOriginRule);
    if (keyInPathIndex < 0){
        ALOGE("handleRedirectPath >> path 中查找规则key中的下标 < 0，index = %zu, path = %s, redirectOriginRule = %s", keyInPathIndex, path, redirectOriginRule)
        return path;
    }

    string resultString(redirectOriginRule);
    string ruleResultString(redirectTargetRule);
    pathString->replace(keyInPathIndex, resultString.length(), ruleResultString, 0, ruleResultString.length());
    ALOGD("===============================================================================================================")
    ALOGD(">> handleRedirectPath >> 文件路径重定向前 origin = %s", path)
    ALOGD(">> handleRedirectPath >> 文件路径重定向后 target = %s", pathString->c_str())
    ALOGD("===============================================================================================================")
    return pathString->c_str();
}

jstring IoRedirect::handleRedirectPath(JNIEnv *env, jstring filePath) {
    if (!sHasInit){
        return filePath;
    }
    const char* path = env->GetStringUTFChars(filePath, JNI_FALSE);
    const char* target =handleRedirectPath(path);
    if (target == nullptr){
        return filePath;
    }
    jstring result= env->NewStringUTF(target);
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

jobject IoRedirect::handleRedirectPath(JNIEnv *env, jobject fileObj) {
    if (!sHasInit){
        return fileObj;
    }
    auto filePath = reinterpret_cast<jstring>(env->CallObjectMethod(fileObj, sFileGetAbsolutePathMethod));
    jstring result = handleRedirectPath(env, filePath);
    if (result == nullptr){
        return fileObj;
    }
    jobject file = env->NewObject(sFileClass, sFileConstructorMethod, result);
    env->DeleteLocalRef(result);
    return file;
}

bool IoRedirect::initJavaIoEnv(JNIEnv *env) {
    sFileClass = env->FindClass(JAVA_FILE_CLASS);
    if (sFileClass == nullptr){
        ALOGE("initJavaIoEnv >> 未找到 %s 类", JAVA_FILE_CLASS)
        env->ExceptionClear();
        return sHasInit = false;
    }
    sFileGetAbsolutePathMethod = env->GetMethodID(sFileClass, JAVA_FILE_GET_ABSOLUTE_PATH_METHOD, JAVA_FILE_GET_ABSOLUTE_PATH_METHOD_SIGN);
    if (sFileGetAbsolutePathMethod == nullptr){
        ALOGE("initJavaIoEnv >> 未找到 %s 方法", JAVA_FILE_GET_ABSOLUTE_PATH_METHOD)
        env->ExceptionClear();
        return sHasInit = false;
    }
    sFileConstructorMethod = env->GetMethodID(sFileClass, "<init>",JAVA_FILE_CONSTRUCTOR_PATH_METHOD_SIGN);
    if (sFileConstructorMethod == nullptr){
        ALOGE("initJavaIoEnv >> 未找到File的构造方法")
        env->ExceptionClear();
        return sHasInit = false;
    }
    return sHasInit = true;
}


