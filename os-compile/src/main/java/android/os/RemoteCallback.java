/*
 * Copyright (C) 2010 The Android Open Source Project
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

package android.os;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @hide
 */
public final class RemoteCallback implements Parcelable {

    public interface OnResultListener {
        void onResult(@Nullable Bundle result);
    }

    private final OnResultListener mListener;
    // @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P)
    private final Handler mHandler;
    // private final IRemoteCallback mCallback;

    public RemoteCallback(OnResultListener listener) {
        this(listener, null);
    }

    public RemoteCallback(@NonNull OnResultListener listener, @Nullable Handler handler) {
        throw new RuntimeException("Stub");
    }

    RemoteCallback(Parcel parcel) {
        throw new RuntimeException("Stub");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        throw new RuntimeException("Stub");
    }

    public static final Parcelable.Creator<RemoteCallback> CREATOR
            = new Parcelable.Creator<RemoteCallback>() {
        public RemoteCallback createFromParcel(Parcel parcel) {
            return new RemoteCallback(parcel);
        }

        public RemoteCallback[] newArray(int size) {
            return new RemoteCallback[size];
        }
    };
}
