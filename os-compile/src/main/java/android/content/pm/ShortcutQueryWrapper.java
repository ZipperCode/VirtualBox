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

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;


/**
 * @hide
 */

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public final class ShortcutQueryWrapper extends LauncherApps.ShortcutQuery implements Parcelable {

    public ShortcutQueryWrapper(LauncherApps.ShortcutQuery query) {
        throw new RuntimeException("Stub");
    }

    @Override
    public void writeToParcel( Parcel dest, int flags) {
        throw new RuntimeException("Stub");
    }

    @Override
    public int describeContents() { return 0; }

    /** @hide */
    /* package-private */ ShortcutQueryWrapper( Parcel in) {
        // You can override field unparcelling by defining methods like:
        // static FieldType unparcelFieldName(Parcel in) { ... }

        throw new RuntimeException("Stub");
    }

    public static final  Parcelable.Creator<ShortcutQueryWrapper> CREATOR
            = new Parcelable.Creator<ShortcutQueryWrapper>() {
        @Override
        public ShortcutQueryWrapper[] newArray(int size) {
            return new ShortcutQueryWrapper[size];
        }

        @Override
        public ShortcutQueryWrapper createFromParcel( Parcel in) {
            return new ShortcutQueryWrapper(in);
        }
    };

}
