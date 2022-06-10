/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.virtual.parsing;

import android.content.pm.ApplicationInfo;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.pm.permission.LegacyPermissionState;
import com.android.server.utils.Snappable;
import com.android.server.utils.Watchable;
import com.android.server.utils.WatchableImpl;
import com.android.server.utils.Watcher;

public abstract class SettingBase {
    // TODO: make this variable protected, or even private with a getter and setter.
    // Simply making it protected or private requires that the name be changed to conformm
    // to the Android naming convention, and that touches quite a few files.
    int pkgFlags;

    // TODO: make this variable protected, or even private with a getter and setter.
    // Simply making it protected or private requires that the name be changed to conformm
    // to the Android naming convention, and that touches quite a few files.
    int pkgPrivateFlags;


    SettingBase(int pkgFlags, int pkgPrivateFlags) {
        setFlags(pkgFlags);
        setPrivateFlags(pkgPrivateFlags);
    }

    SettingBase(SettingBase orig) {
        doCopy(orig);
    }

    public void copyFrom(SettingBase orig) {
        doCopy(orig);
    }

    private void doCopy(SettingBase orig) {
        pkgFlags = orig.pkgFlags;
        pkgPrivateFlags = orig.pkgPrivateFlags;
    }
    void setFlags(int pkgFlags) {
        this.pkgFlags = pkgFlags
                & (ApplicationInfo.FLAG_SYSTEM
                        | ApplicationInfo.FLAG_EXTERNAL_STORAGE);
    }

    void setPrivateFlags(int pkgPrivateFlags) {
        this.pkgPrivateFlags = pkgPrivateFlags
                & (ApplicationInfo.PRIVATE_FLAG_PRIVILEGED
                | ApplicationInfo.PRIVATE_FLAG_OEM
                | ApplicationInfo.PRIVATE_FLAG_VENDOR
                | ApplicationInfo.PRIVATE_FLAG_PRODUCT
                | ApplicationInfo.PRIVATE_FLAG_SYSTEM_EXT
                | ApplicationInfo.PRIVATE_FLAG_REQUIRED_FOR_SYSTEM_USER
                | ApplicationInfo.PRIVATE_FLAG_ODM);
    }
}