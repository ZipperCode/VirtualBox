/*
 * Copyright (C) 2020 The Android Open Source Project
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

package android.content.pm;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @hide
 */
public class LauncherActivityInfoInternal implements Parcelable {
    
    @NonNull
    private ActivityInfo mActivityInfo;
    @NonNull private ComponentName mComponentName;

    public LauncherActivityInfoInternal(Parcel source) {
        throw new RuntimeException("Stub");
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public ActivityInfo getActivityInfo() {
        return mActivityInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new RuntimeException("Stub");
    }

    public static final Creator<LauncherActivityInfoInternal> CREATOR =
            new Creator<LauncherActivityInfoInternal>() {
        public LauncherActivityInfoInternal createFromParcel(Parcel source) {
            return new LauncherActivityInfoInternal(source);
        }
        public LauncherActivityInfoInternal[] newArray(int size) {
            return new LauncherActivityInfoInternal[size];
        }
    };
}
