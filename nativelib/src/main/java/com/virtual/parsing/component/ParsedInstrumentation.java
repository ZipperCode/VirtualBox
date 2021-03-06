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


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.virtual.box.base.ext.StringUtils;


public class ParsedInstrumentation extends ParsedComponent {

    @Nullable
    private String targetPackage;
    @Nullable
    private String targetProcesses;
    boolean handleProfiling;
    boolean functionalTest;

    public ParsedInstrumentation() {
    }

    public void setTargetPackage(@Nullable String targetPackage) {
        this.targetPackage = StringUtils.safeIntern(targetPackage);
    }

    public void setTargetProcesses(@Nullable String targetProcesses) {
        this.targetProcesses = StringUtils.safeIntern(targetProcesses);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Instrumentation{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ')
                .append(getPackageName())
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
        sForInternedString.parcel(this.targetPackage, dest, flags);
        sForInternedString.parcel(this.targetProcesses, dest, flags);
        dest.writeByte((byte) (this.handleProfiling ? 1 : 0));
        dest.writeByte((byte) (this.functionalTest ? 1 : 0));
    }

    protected ParsedInstrumentation(Parcel in) {
        super(in);
        this.targetPackage = sForInternedString.unparcel(in);
        this.targetProcesses = sForInternedString.unparcel(in);
        this.handleProfiling = in.readByte() != 0;
        this.functionalTest = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ParsedInstrumentation> CREATOR =
            new Parcelable.Creator<ParsedInstrumentation>() {
                @Override
                public ParsedInstrumentation createFromParcel(Parcel source) {
                    return new ParsedInstrumentation(source);
                }

                @Override
                public ParsedInstrumentation[] newArray(int size) {
                    return new ParsedInstrumentation[size];
                }
            };

    @Nullable
    public String getTargetPackage() {
        return targetPackage;
    }

    @Nullable
    public String getTargetProcesses() {
        return targetProcesses;
    }

    public boolean isHandleProfiling() {
        return handleProfiling;
    }

    public boolean isFunctionalTest() {
        return functionalTest;
    }
}