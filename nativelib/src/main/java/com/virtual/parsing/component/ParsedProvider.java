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

package com.virtual.parsing.component;

import android.content.pm.PathPermission;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.box.base.ext.StringUtils;


/**
 * @hide
 **/
public class ParsedProvider extends ParsedMainComponent {

    @NonNull
    private String authority;
    boolean syncable;
    @Nullable
    private String readPermission;
    @Nullable
    private String writePermission;
    boolean grantUriPermissions;
    boolean forceUriPermissions;
    boolean multiProcess;
    int initOrder;
    @Nullable
    PatternMatcher[] uriPermissionPatterns;
    @Nullable
    PathPermission[] pathPermissions;

    public ParsedProvider(ParsedProvider other) {
        super(other);

        this.authority = other.authority;
        this.syncable = other.syncable;
        this.readPermission = other.readPermission;
        this.writePermission = other.writePermission;
        this.grantUriPermissions = other.grantUriPermissions;
        this.forceUriPermissions = other.forceUriPermissions;
        this.multiProcess = other.multiProcess;
        this.initOrder = other.initOrder;
        this.uriPermissionPatterns = other.uriPermissionPatterns;
        this.pathPermissions = other.pathPermissions;
    }

    public void setAuthority(String authority) {
        this.authority = StringUtils.safeIntern(authority);
    }

    public void setSyncable(boolean syncable) {
        this.syncable = syncable;
    }

    public void setReadPermission(String readPermission) {
        // Empty string must be converted to null
        this.readPermission = TextUtils.isEmpty(readPermission)
                ? null : readPermission.intern();
    }

    public void setWritePermission(String writePermission) {
        // Empty string must be converted to null
        this.writePermission = TextUtils.isEmpty(writePermission)
                ? null : writePermission.intern();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Provider{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ').append(getPackageName())
                .append("#")
                .append(getName());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.authority);
        dest.writeByte((byte) (this.syncable ? 1 : 0));
        sForInternedString.parcel(this.readPermission, dest, flags);
        sForInternedString.parcel(this.writePermission, dest, flags);
        dest.writeByte((byte) (this.grantUriPermissions ? 1 : 0));
        dest.writeByte((byte) (this.forceUriPermissions ? 1 : 0));
        dest.writeByte((byte) (this.multiProcess ? 1 : 0));
        dest.writeInt(this.initOrder);
        dest.writeTypedArray(this.uriPermissionPatterns, flags);
        dest.writeTypedArray(this.pathPermissions, flags);
    }

    public ParsedProvider() {
    }

    protected ParsedProvider(Parcel in) {
        super(in);
        //noinspection ConstantConditions
        this.authority = in.readString();
        this.syncable = in.readByte() == 1;
        this.readPermission = sForInternedString.unparcel(in);
        this.writePermission = sForInternedString.unparcel(in);
        this.grantUriPermissions = in.readByte() == 1;
        this.forceUriPermissions = in.readByte() == 1;
        this.multiProcess = in.readByte() == 1;
        this.initOrder = in.readInt();
        this.uriPermissionPatterns = in.createTypedArray(PatternMatcher.CREATOR);
        this.pathPermissions = in.createTypedArray(PathPermission.CREATOR);
    }

    public static final Parcelable.Creator<ParsedProvider> CREATOR = new Creator<ParsedProvider>() {
        @Override
        public ParsedProvider createFromParcel(Parcel source) {
            return new ParsedProvider(source);
        }

        @Override
        public ParsedProvider[] newArray(int size) {
            return new ParsedProvider[size];
        }
    };

    @NonNull
    public String getAuthority() {
        return authority;
    }

    public boolean isSyncable() {
        return syncable;
    }

    @Nullable
    public String getReadPermission() {
        return readPermission;
    }

    @Nullable
    public String getWritePermission() {
        return writePermission;
    }

    public boolean isGrantUriPermissions() {
        return grantUriPermissions;
    }

    public boolean isForceUriPermissions() {
        return forceUriPermissions;
    }

    public boolean isMultiProcess() {
        return multiProcess;
    }

    public int getInitOrder() {
        return initOrder;
    }

    @Nullable
    public PatternMatcher[] getUriPermissionPatterns() {
        return uriPermissionPatterns;
    }

    @Nullable
    public PathPermission[] getPathPermissions() {
        return pathPermissions;
    }
}