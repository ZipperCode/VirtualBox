#include "ArtMethodHandle.h"

__attribute__((section (".mytext")))  JNICALL void native_offset
        (JNIEnv *env, jclass obj) {
}

__attribute__((section (".mytext")))  JNICALL void native_offset2
        (JNIEnv *env, jclass obj) {
}

JNIEnv *ArtMethodHandle::sJniEnv = nullptr;
int ArtMethodHandle::sAndroidLevel = 0;
bool ArtMethodHandle::sInitArtMethodOffsetStatus = false;
uint32_t ArtMethodHandle::sArtMethodAccFlagOffset = 0;
uint32_t ArtMethodHandle::sArtMethodNativeOffset = 0;
uint32_t ArtMethodHandle::sArtMethodSize = 0;

uint32_t ArtMethodHandle::sArtMethodDexCodeItemOffset = 0;
uint32_t ArtMethodHandle::sArtMethodDexCodeItemOffsetValue = 0;
uint32_t ArtMethodHandle::sArtMethodDexMethodIndexOffset = 0;
uint32_t ArtMethodHandle::sArtMethodDexMethodIndexValue = 0;


int ArtMethodHandle::initArtMethod(JNIEnv *env, int android_level) {
    if (registerArtMethod(env) == JNI_FALSE) {
        ALOGE("register java art method fail");
        return JNI_FALSE;
    }
    ArtMethodHandle::sAndroidLevel = android_level;
    jclass clazz = env->FindClass(JAVA_ART_METHOD);
    jmethodID offset1MethodId = env->GetStaticMethodID(clazz, OFFSET_METHOD_1, OFFSET_METHOD_SIGN);
    jmethodID offset2MethodId = env->GetStaticMethodID(clazz, OFFSET_METHOD_2, OFFSET_METHOD_SIGN);
    // 获取jni方法对应的方法内存指针
    void *nativeOffset = getArtMethodPtr(env, clazz, offset1MethodId);
    void *nativeOffset2 = getArtMethodPtr(env, clazz, offset2MethodId);

    ALOGD("ArtMethod >> offset1MethodId = %p, nativeOffset = %p", offset1MethodId, nativeOffset)
    ALOGD("ArtMethod >> offset2MethodId = %p, nativeOffset2 = %p", offset1MethodId, nativeOffset)
    // 得到两个相邻Native方法直接的偏移得出ArtMethod的大小
    sArtMethodSize = (size_t) nativeOffset2 - (size_t) nativeOffset;
    auto jniOffset1Method = native_offset;
    auto artMethod = reinterpret_cast<uint32_t *>(nativeOffset);
    for (int i = 0; i < sArtMethodSize; i++) {
        if (reinterpret_cast<void *>(artMethod[i]) == reinterpret_cast<void*>(jniOffset1Method)) {
            sArtMethodNativeOffset = i;
            break;
        }
    }
    if (sArtMethodNativeOffset == 0) {
        ALOGE("未计算出ArtMethod在C方法中的偏移 sArtMethodNativeOffset == 0")
        sInitArtMethodOffsetStatus = false;
        return JNI_FALSE;
    }

    //class ArtMethod {
    //            uint32_t declaring_class_;
    //            uint32_t access_flags_;
    //            uint32_t dex_code_item_offset_;
    //            uint32_t dex_method_index_;
    //            uint16_t method_index_;
    //            uint16_t hotness_count_;
    //};
    auto pArtMethodStartUInt32 = reinterpret_cast<uint32_t *>(nativeOffset);
    // 结构值，反射java方法的到值
    auto customAccFlag = getJavaOffsetMethodAccFlag(env);
    for (int i = 0; i < sArtMethodSize; ++i) {
        uint32_t value = *(pArtMethodStartUInt32 + i);
        if (value == customAccFlag){
            // 得到访问标识符的位置
            sArtMethodAccFlagOffset = i;
            uint32_t dex_code_item_offset_ = i + 1;
            uint32_t dex_method_index_ = i + 2;
            sArtMethodDexCodeItemOffsetValue = *(pArtMethodStartUInt32 + dex_code_item_offset_);
            sArtMethodDexCodeItemOffset = dex_code_item_offset_;
            sArtMethodDexMethodIndexValue = *(pArtMethodStartUInt32 + dex_method_index_);
            sArtMethodDexMethodIndexOffset = dex_method_index_;
            ALOGD("ArtMethod >> dex_code_item_offset_ = %u", dex_code_item_offset_)
            ALOGD("ArtMethod >> dex_method_index_ = %u", dex_method_index_)
            break;
        }
    }
    if (sArtMethodAccFlagOffset <= 0){
        if (android_level >= __ANDROID_API_N__) {
            sArtMethodAccFlagOffset = 4 / sizeof(uint32_t);
        }else if (android_level == __ANDROID_API_M__){
            sArtMethodAccFlagOffset = 12 / sizeof(uint32_t);
        }else if (android_level == __ANDROID_API_L_MR1__){
            sArtMethodAccFlagOffset = 20 / sizeof(uint32_t);
        }else if (android_level == __ANDROID_API_L__){
            sArtMethodAccFlagOffset = 56 / sizeof(uint32_t);
        }
    }
    toString();
    sInitArtMethodOffsetStatus = true;
    return JNI_TRUE;
}


uint32_t ArtMethodHandle::getAccessFlags(void *pArtMethod) {
    return *(reinterpret_cast<uint32_t *>(pArtMethod) + sArtMethodAccFlagOffset);
}

bool ArtMethodHandle::setAccessFlags(void *pArtMethod, uint32_t flags) {
    *(reinterpret_cast<uint32_t *>(pArtMethod) + sArtMethodAccFlagOffset) = flags;
    return true;
}

long ArtMethodHandle::calculateArtMethodFlag(void *pArtMethod) {

    return 0;
}

void ArtMethodHandle::addNativeAccessFlag(void *pArtMethod) {
    uint32_t oldFlag = getAccessFlags(pArtMethod);
    uint32_t newFlag = oldFlag | kAccNative;
    setAccessFlags(pArtMethod, newFlag);
}

void ArtMethodHandle::addAccessFlags(void *pArtMethod, uint32_t flag) {
    uint32_t oldFlags = getAccessFlags(pArtMethod);
    uint32_t newFlags = oldFlags | flag;
    setAccessFlags(pArtMethod, newFlags);
}

bool ArtMethodHandle::checkNativeMethod(void *pArtMethod) {
    uint32_t oldFlags = getAccessFlags(pArtMethod);
    return (oldFlags & kAccNative) != 0;
}

bool ArtMethodHandle::clearFastNativeFlag(void *art_method) {
    // FastNative
    return sAndroidLevel < __ANDROID_API_P__ &&
           clearAccessFlag(art_method, kAccFastNative);
}

/**
 * 清除方法的访问标识符
 * @param art_method    art方法指针
 * @param flag          标识符
 * @return
 */
bool ArtMethodHandle::clearAccessFlag(void *art_method, uint32_t flag) {
    uint32_t old_flag = getAccessFlags(art_method);
    uint32_t new_flag = old_flag & ~flag;
    return new_flag != old_flag && setAccessFlags(art_method, new_flag);
}

void *ArtMethodHandle::getArtMethodPtr(JNIEnv *env, jclass clazz, jmethodID methodId) {
    // Android11 后获取artMethod指针通过反射，11以前methodId就是ArtMethod指针
    if (sAndroidLevel >= __ANDROID_API_Q__) {
        jclass executable = env->FindClass("java/lang/reflect/Executable");
        jfieldID artId = env->GetFieldID(executable, "artMethod", "J");
        jobject method = env->ToReflectedMethod(clazz, methodId, true);
        return reinterpret_cast<void *>(env->GetLongField(method, artId));
    } else {
        return methodId;
    }
}

uint32_t ArtMethodHandle::getJavaOffsetMethodAccFlag(JNIEnv *env) {
    uint32_t accFlag = 0;
    jclass executable = env->FindClass("java/lang/reflect/Executable");
    jfieldID accFlagFieldId = env->GetFieldID(executable, "accessFlags", "I");
    jclass javaArtMethodCls = env->FindClass(JAVA_ART_METHOD);
    jmethodID offset1MethodId = env->GetStaticMethodID(javaArtMethodCls, OFFSET_METHOD_1, OFFSET_METHOD_SIGN);
    jobject methodObj = env->ToReflectedMethod(javaArtMethodCls, offset1MethodId, true);
    accFlag = env->GetIntField(methodObj, accFlagFieldId);
    return accFlag;
}

int ArtMethodHandle::registerArtMethod(JNIEnv *env) {
    jclass clazz = env->FindClass(JAVA_ART_METHOD);
    JNINativeMethod gNativeArtMethods[] = {
            {OFFSET_METHOD_1, OFFSET_METHOD_SIGN, (void *) native_offset},
            {OFFSET_METHOD_2, OFFSET_METHOD_SIGN, (void *) native_offset2},
    };
    if (env->RegisterNatives(clazz, gNativeArtMethods, sizeof(gNativeArtMethods) / sizeof(gNativeArtMethods[0])) < 0) {
        ALOGE("jni register error.");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

void ArtMethodHandle::toString() {
    ALOGD("========================== ArtMethod Start ==========================")
    ALOGD(">> sAndroidLevel                 = %d", sAndroidLevel)
    ALOGD(">> sInitArtMethodOffsetStatus    = %d", sInitArtMethodOffsetStatus)
    ALOGD(">> sArtMethodAccFlagOffset       = %d", sArtMethodAccFlagOffset)
    ALOGD(">> sArtMethodNativeOffset        = %d", sArtMethodNativeOffset)
    ALOGD(">> sArtMethodSize                = %d", sArtMethodSize)
    ALOGD("========================== ArtMethod End ==========================")
}

void ArtMethodHandle::printArtMethod(const char *name, void *pArtMethod) {
    ALOGD("========================== ArtMethod Struct ==========================")
    ALOGD(">> pArtMethod Name                    = %s", name)
    ALOGD(">> pArtMethod Address                 = %p", pArtMethod)
    if (sAndroidLevel >= __ANDROID_API_Q__) {
        ALOGD(">> pArtMethod JavaMethodId            = %p", reinterpret_cast<uint8_t *>(pArtMethod) - sArtMethodNativeOffset)
    } else {
        ALOGD(">> pArtMethod JavaMethodId            = %p", pArtMethod)
    }
    ALOGD(">> pArtMethod AccessFlags             = %d", getAccessFlags(pArtMethod))
    ALOGD(">> pArtMethod DexCodeItemOffset       = %p", reinterpret_cast<uint8_t *>(pArtMethod) + sArtMethodDexCodeItemOffset)
    ALOGD(">> pArtMethod DexCodeItemOffsetValue  = %d", *(reinterpret_cast<uint8_t *>(pArtMethod) + sArtMethodDexCodeItemOffset))
    ALOGD(">> pArtMethod DexMethodIndex          = %d", *(reinterpret_cast<uint8_t *>(pArtMethod) + sArtMethodDexMethodIndexOffset))
    ALOGD("========================== ArtMethod Struct ==========================")
}















