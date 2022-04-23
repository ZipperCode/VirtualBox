#ifndef VIRTUAL_BOX_JNI_ENV_REF_H
#define VIRTUAL_BOX_JNI_ENV_REF_H

#include <jni.h>

class JniEnvRef{
private:
    JavaVM *mVm;
    JNIEnv *mEnv;
public:
    JniEnvRef(JavaVM *vm): mVm(vm){
        JNIEnv *env;
        vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
        if(env == nullptr){
            mVm->AttachCurrentThread(&env, nullptr);
        }
        this->mEnv = env;
    }

    ~JniEnvRef(){
        this->mVm->DetachCurrentThread();
        this->mEnv = nullptr;
    }

    JNIEnv* operator->(){
        return this->mEnv;
    }

    JNIEnv* get(){
        return this->mEnv;
    }

    bool isNull() const{
        return this->mVm == nullptr;
    }

    bool isNotNull() const{
        return !isNull();
    }
};

#endif