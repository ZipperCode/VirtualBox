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
size_t ArtMethodHandle::sArtMethodAccFlagOffset = 0;
size_t ArtMethodHandle::sArtMethodNativeOffset = 0;
size_t ArtMethodHandle::sArtMethodSize = 0;

uint32_t ArtMethodHandle::sArtMethodDexCodeItemOffset = 0;
uint32_t ArtMethodHandle::sArtMethodDexCodeItemOffsetValue = 0;
uint32_t ArtMethodHandle::sArtMethodDexMethodIndexOffset = 0;
uint32_t ArtMethodHandle::sArtMethodDexMethodIndexValue = 0;


int ArtMethodHandle::InitArtMethod(JNIEnv *env, int android_level) {
    if (registerArtMethod(env) == JNI_FALSE) {
        ALOGE("register java art method fail");
        return JNI_FALSE;
    }
    ArtMethodHandle::sAndroidLevel = android_level;
    jclass clazz = env->FindClass(JAVA_ART_METHOD);
    jmethodID offset1MethodId = env->GetStaticMethodID(clazz, OFFSET_METHOD_1, OFFSET_METHOD_SIGN);
    jmethodID offset2MethodId = env->GetStaticMethodID(clazz, OFFSET_METHOD_2, OFFSET_METHOD_SIGN);
    // 获取jni方法对应的方法内存指针
    void *nativeOffset = GetArtMethodPtr(env, clazz, offset1MethodId);
    void *nativeOffset2 = GetArtMethodPtr(env, clazz, offset2MethodId);

    ALOGD("ArtMethod >> offset1MethodId = %p, nativeOffset = %p", offset1MethodId, nativeOffset)
    ALOGD("ArtMethod >> offset2MethodId = %p, nativeOffset2 = %p", offset1MethodId, nativeOffset)
    // 得到两个相邻Native方法直接的偏移得出ArtMethod的大小
    sArtMethodSize = (size_t) nativeOffset2 - (size_t) nativeOffset;
    auto jniOffset1Method = native_offset;
    auto artMethod = reinterpret_cast<uintptr_t *>(nativeOffset);
    for (int i = 0; i < sArtMethodSize; ++i) {
        if (reinterpret_cast<void *>(artMethod[i]) == jniOffset1Method) {
            sArtMethodNativeOffset = i;
            break;
        }
    }
    if (sArtMethodNativeOffset == 0){
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
    auto pArtMethodStart = reinterpret_cast<uint32_t *>(artMethod);
    auto customAccFlag = GetOffsetMethodFlag();
    for (int i = 0; i < sArtMethodSize; ++i) {
        auto offset = i * sizeof(uint32_t);
        // 结构值，得到内存值
        auto value = *(pArtMethodStart + offset);
        if (value == customAccFlag){
            // 得到访问标识符的位置
            sArtMethodAccFlagOffset = offset;
            uint32_t dex_code_item_offset_ = (offset + sizeof(uint32_t));
            uint32_t dex_method_index_ = offset + 2 * sizeof(uint32_t);
            sArtMethodDexCodeItemOffsetValue = *( pArtMethodStart + dex_code_item_offset_);
            sArtMethodDexCodeItemOffset = dex_code_item_offset_;
            sArtMethodDexMethodIndexValue = *(pArtMethodStart + dex_method_index_);
            sArtMethodDexMethodIndexOffset = dex_method_index_;
            ALOGD("ArtMethod >> dex_code_item_offset_ = %u", dex_code_item_offset_)
            ALOGD("ArtMethod >> dex_method_index_ = %u", dex_method_index_)
            break;
        }
    }
    toString();
    sInitArtMethodOffsetStatus = true;
    return JNI_TRUE;
}


uint32_t ArtMethodHandle::GetAccessFlags(const uint32_t *pArtMethod) {
    return *(pArtMethod + getArtMethodAccFlagOffset());
}

bool ArtMethodHandle::SetAccessFlags(uint32_t *pArtMethod, uint32_t flags) {
    *(pArtMethod + getArtMethodAccFlagOffset()) = flags;
    return true;
}

long ArtMethodHandle::CalculateArtMethodFlag(uint32_t *pArtMethod) {

    return 0;
}

void ArtMethodHandle::AddNativeAccessFlag(uint32_t *pArtMethod) {
    uint32_t oldFlag = GetAccessFlags(pArtMethod);
    uint32_t newFlag = oldFlag | kAccNative;
    SetAccessFlags(pArtMethod, newFlag);
}

void ArtMethodHandle::AddAccessFlags(uintptr_t *pArtMethod, uint32_t flag) {
    uint32_t oldFlags = GetAccessFlags(pArtMethod);
    uint32_t newFlags = oldFlags | flag;
    SetAccessFlags(pArtMethod, newFlags);
}

bool ArtMethodHandle::CheckNativeMethod(uintptr_t *pArtMethod) {
    uint32_t oldFlags = GetAccessFlags(pArtMethod);
    return (oldFlags & kAccNative) == oldFlags;
}

bool ArtMethodHandle::ClearFastNativeFlag(uintptr_t *art_method) {
    // FastNative
    return sAndroidLevel < __ANDROID_API_P__ &&
           ClearAccessFlag(art_method, kAccFastNative);
}

/**
 * 清除方法的访问标识符
 * @param art_method    art方法指针
 * @param flag          标识符
 * @return
 */
bool ArtMethodHandle::ClearAccessFlag(uintptr_t *art_method, uint32_t flag) {
    uint32_t old_flag = GetAccessFlags(art_method);
    uint32_t new_flag = old_flag & ~flag;
    return new_flag != old_flag && SetAccessFlags(art_method, new_flag);
}

void *ArtMethodHandle::GetArtMethodPtr(JNIEnv *env, jclass clazz, jmethodID methodId) {
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

void ArtMethodHandle::printArtMethod(uintptr_t *pArtMethod) {
    toString();
    ALOGD("========================== ArtMethod Struct ==========================")
    ALOGD(">> pArtMethod Address                 = %p", pArtMethod)
    if (sAndroidLevel >= __ANDROID_API_Q__) {
        ALOGD(">> pArtMethod JavaMethodId            = %p", pArtMethod - sArtMethodNativeOffset)
    }else{
        ALOGD(">> pArtMethod JavaMethodId            = %p", pArtMethod)
    }
    ALOGD(">> pArtMethod AccessFlags             = %x", GetAccessFlags(pArtMethod))
    ALOGD(">> pArtMethod DexCodeItemOffset       = %d", *(pArtMethod + sArtMethodDexCodeItemOffset))
    ALOGD(">> pArtMethod DexMethodIndex          = %d", *(pArtMethod + sArtMethodDexMethodIndexOffset))
    ALOGD("========================== ArtMethod Struct ==========================")
}















