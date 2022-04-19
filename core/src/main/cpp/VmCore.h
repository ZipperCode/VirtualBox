//
// Created by Zipper on 2022/4/19.
//

#ifndef VIRTUALBOX_VMCORE_H
#define VIRTUALBOX_VMCORE_H
#include <jni.h>
#include "util/log.h"
#define VM_CORE_CLASS "com/virtual/box/core/hook/core/VmCore"

const static JNINativeMethod jniNativeMethods[] = {

};

class VmCore{
public:
    static int android_level;
    static JavaVM *vm;
    static JNIEnv *env;
    static jclass vmCoreClass;
public:
    /**
     * 虚拟应用核心类，处理NativeHook
     * @return
     */
    static int init();

private:
    static int registerNativeMethod();
};

#endif //VIRTUALBOX_VMCORE_H
