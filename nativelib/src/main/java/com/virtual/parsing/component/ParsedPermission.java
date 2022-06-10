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

import android.content.pm.PermissionInfo;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.virtual.box.base.ext.StringUtils;
import com.virtual.box.util.CompatArraySet;
import com.virtual.box.util.Parcelling;

import java.util.Locale;
import java.util.Set;

/** @hide */
public class ParsedPermission extends ParsedComponent {

    private static Parcelling.BuiltIn.ForStringSet sForStringSet = Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForStringSet.class);

    @Nullable
    String backgroundPermission;
    @Nullable
    private String group;
    int requestRes;
    int protectionLevel;
    boolean tree;
    @Nullable
    private ParsedPermissionGroup parsedPermissionGroup;
    @Nullable
    Set<String> knownCerts;

    @VisibleForTesting
    public ParsedPermission() {
    }

    public ParsedPermission(ParsedPermission other) {
        super(other);
        this.backgroundPermission = other.backgroundPermission;
        this.group = other.group;
        this.requestRes = other.requestRes;
        this.protectionLevel = other.protectionLevel;
        this.tree = other.tree;
        this.parsedPermissionGroup = other.parsedPermissionGroup;
    }

    public ParsedPermission setGroup(String group) {
        this.group = StringUtils.safeIntern(group);
        return this;
    }

    public ParsedPermission setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public boolean isRuntime() {
        return getProtection() == PermissionInfo.PROTECTION_DANGEROUS;
    }

    public boolean isAppOp() {
        return (protectionLevel & PermissionInfo.PROTECTION_FLAG_APPOP) != 0;
    }

    public int getProtection() {
        return protectionLevel & PermissionInfo.PROTECTION_MASK_BASE;
    }

    public int getProtectionFlags() {
        return protectionLevel & ~PermissionInfo.PROTECTION_MASK_BASE;
    }

    public @Nullable Set<String> getKnownCerts() {
        return knownCerts;
    }

    protected void setKnownCert(String knownCert) {
        // Convert the provided digest to upper case for consistent Set membership
        // checks when verifying the signing certificate digests of requesting apps.
        CompatArraySet<String> strings = new CompatArraySet<>();
        strings.add(knownCert.toUpperCase(Locale.US));
        this.knownCerts = strings;
    }

    protected void setKnownCerts(String[] knownCerts) {
        this.knownCerts = new CompatArraySet<>();
        for (String knownCert : knownCerts) {
            this.knownCerts.add(knownCert.toUpperCase(Locale.US));
        }
    }

    public int calculateFootprint() {
        return getName().length();
    }

    @Override
    public String toString() {
        return "Permission{"
                + Integer.toHexString(System.identityHashCode(this))
                + " " + getName() + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.backgroundPermission);
        dest.writeString(this.group);
        dest.writeInt(this.requestRes);
        dest.writeInt(this.protectionLevel);
        dest.writeByte((byte) (this.tree ? 1 : 0));
        dest.writeParcelable(this.parsedPermissionGroup, flags);
        sForStringSet.parcel(knownCerts, dest, flags);
    }

    protected ParsedPermission(Parcel in) {
        super(in);
        // We use the boot classloader for all classes that we load.
        final ClassLoader boot = Object.class.getClassLoader();
        this.backgroundPermission = in.readString();
        this.group = in.readString();
        this.requestRes = in.readInt();
        this.protectionLevel = in.readInt();
        this.tree = in.readByte() == 1;
        this.parsedPermissionGroup = in.readParcelable(boot);
        this.knownCerts = sForStringSet.unparcel(in);
    }

    public static final Parcelable.Creator<ParsedPermission> CREATOR =
            new Parcelable.Creator<ParsedPermission>() {
                @Override
                public ParsedPermission createFromParcel(Parcel source) {
                    return new ParsedPermission(source);
                }

                @Override
                public ParsedPermission[] newArray(int size) {
                    return new ParsedPermission[size];
                }
            };

    @Nullable
    public String getBackgroundPermission() {
        return backgroundPermission;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    public int getRequestRes() {
        return requestRes;
    }

    public int getProtectionLevel() {
        return protectionLevel;
    }

    public boolean isTree() {
        return tree;
    }

    @Nullable
    public ParsedPermissionGroup getParsedPermissionGroup() {
        return parsedPermissionGroup;
    }

    public ParsedPermission setProtectionLevel(int value) {
        protectionLevel = value;
        return this;
    }

    public ParsedPermission setParsedPermissionGroup(@Nullable ParsedPermissionGroup value) {
        parsedPermissionGroup = value;
        return this;
    }
}