//
// Created by Zipper on 2022/4/19.
//

#ifndef VIRTUALBOX_LOG_H
#define VIRTUALBOX_LOG_H

#include <android/log.h>
#define TAG "VirtualBox"

#define DEBUG 1

#if DEBUG
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#else
#define ALOGD(...)
#define ALOGE(...)
#endif

#endif //VIRTUALBOX_LOG_H
