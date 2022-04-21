//
// Created by Zipper on 2022/4/19.
//

#ifndef VIRTUALBOX_VMCORE_H
#define VIRTUALBOX_VMCORE_H
#include <jni.h>
#include "utils/log.h"
#include "ArtMethodHandle.h"

#define VM_CORE_CLASS "com/virtual/box/core/hook/core/VmCore"

int initVm(int android_level);

/**
 * register VmCore class Native Method
 * @return register success true, fail false
 */
int registerVmNativeMethod();

int registerOffsetNativeMethod();

static struct {
    int android_level;
    JavaVM *vm;
    JNIEnv *env;
    jclass vmCoreClass;
}VmEnv;
#endif //VIRTUALBOX_VMCORE_H
