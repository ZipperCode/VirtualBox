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

import static java.util.Collections.emptyMap;

import android.content.ComponentName;
import android.content.pm.PackageManager.Property;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.virtual.box.base.ext.StringUtils;
import com.virtual.box.reflect.MirrorReflection;
import com.virtual.box.util.Parcelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** @hide */
public abstract class ParsedComponent implements Parcelable {

    private final static ParsedIntentInfo.ListParceler sForIntentInfos = Parcelling.Cache.getOrCreate(
            ParsedIntentInfo.ListParceler.class);

    protected final static Parcelling.BuiltIn.ForInternedString sForInternedString = new Parcelling.BuiltIn.ForInternedString();

    @NonNull
    private String name;
    int icon;
    int labelRes;
    int logo;
    int banner;
    int descriptionRes;

    // TODO(b/135203078): Replace flags with individual booleans, scoped by subclass
    int flags;

    @NonNull
    private String packageName;

    @Nullable
    private List<ParsedIntentInfo> intents;

    private ComponentName componentName;

    @Nullable
    protected Bundle metaData;

    private Map<String, Property> mProperties = emptyMap();

    ParsedComponent() {

    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public ParsedComponent(ParsedComponent other) {
        this.metaData = other.metaData;
        this.name = other.name;
        this.icon = other.getIcon();
        this.labelRes = other.getLabelRes();
        this.logo = other.getLogo();
        this.banner = other.getBanner();

        this.descriptionRes = other.getDescriptionRes();

        this.flags = other.getFlags();

        this.setPackageName(other.packageName);
        this.intents = new ArrayList<>(other.getIntents());
    }

    public void addIntent(ParsedIntentInfo intent) {
        if (intents == null){
            this.intents = new ArrayList<>();
        }
        this.intents.add(intent);
    }

    /** Add a property to the component */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void addProperty(@NonNull Property property) {
        if (this.mProperties == null){
            this.mProperties = new ArrayMap<>();
        }
        this.mProperties.put(property.getName(), property);
    }

    @NonNull
    public List<ParsedIntentInfo> getIntents() {
        return intents != null ? intents : Collections.emptyList();
    }

    public ParsedComponent setName(String name) {
        this.name = StringUtils.safeIntern(name);
        return this;
    }

    @CallSuper
    public void setPackageName(@NonNull String packageName) {
        this.packageName = StringUtils.safeIntern(packageName);
        //noinspection ConstantConditions
        this.componentName = null;

        // Note: this method does not edit name (which can point to a class), because this package
        // name change is not changing the package in code, but the identifier used by the system.
    }

    @NonNull
    public ComponentName getComponentName() {
        if (componentName == null) {
            componentName = new ComponentName(getPackageName(), getName());
        }
        return componentName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.getIcon());
        dest.writeInt(this.getLabelRes());
        dest.writeInt(this.getLogo());
        dest.writeInt(this.getBanner());
        dest.writeInt(this.getDescriptionRes());
        dest.writeInt(this.getFlags());
        sForInternedString.parcel(this.packageName, dest, flags);
        sForIntentInfos.parcel(this.getIntents(), dest, flags);
        dest.writeBundle(this.metaData);
        dest.writeMap(this.mProperties);
    }

    protected ParsedComponent(Parcel in) {
        // We use the boot classloader for all classes that we load.
        final ClassLoader boot = Object.class.getClassLoader();
        //noinspection ConstantConditions
        this.name = in.readString();
        this.icon = in.readInt();
        this.labelRes = in.readInt();
        this.logo = in.readInt();
        this.banner = in.readInt();
        this.descriptionRes = in.readInt();
        this.flags = in.readInt();
        //noinspection ConstantConditions
        this.packageName = sForInternedString.unparcel(in);
        this.intents = sForIntentInfos.unparcel(in);
        this.metaData = in.readBundle(boot);
        this.mProperties = in.readHashMap(boot);
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getLabelRes() {
        return labelRes;
    }

    public int getLogo() {
        return logo;
    }

    public int getBanner() {
        return banner;
    }

    public int getDescriptionRes() {
        return descriptionRes;
    }

    public int getFlags() {
        return flags;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    @Nullable
    public Bundle getMetaData() {
        return metaData;
    }

    @NonNull
    public Map<String, Property> getProperties() {
        return mProperties;
    }
}