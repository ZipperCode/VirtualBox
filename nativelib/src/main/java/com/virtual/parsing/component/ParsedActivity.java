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

import static android.content.pm.ActivityInfo.RESIZE_MODE_FORCE_RESIZEABLE;
import static android.content.pm.ActivityInfo.RESIZE_MODE_RESIZEABLE;
import static android.content.pm.ActivityInfo.RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.content.pm.parsing.ParsingPackageImpl.sForInternedString;
import static android.view.WindowManager.LayoutParams.ROTATION_ANIMATION_UNSPECIFIED;

import android.app.ActivityTaskManager;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.virtual.box.base.ext.StringUtils;


/** @hide **/
public class ParsedActivity extends ParsedMainComponent {

    public static final int RESIZE_MODE_UNRESIZEABLE = 0;
    /**
     * Activity didn't explicitly request to be resizeable, but we are making it resizeable because
     * of the SDK version it targets. Only affects apps with target SDK >= N where the app is
     * implied to be resizeable if it doesn't explicitly set the attribute to any value.
     * @hide
     */
    public static final int RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION = 1;
    /**
     * Activity explicitly requested to be resizeable.
     * @hide
     */
    public static final int RESIZE_MODE_RESIZEABLE = 2;
    /**
     * Activity is resizeable and supported picture-in-picture mode.  This flag is now deprecated
     * since activities do not need to be resizeable to support picture-in-picture.
     * See {#FLAG_SUPPORTS_PICTURE_IN_PICTURE}.
     *
     * @hide
     * @deprecated
     */
    public static final int RESIZE_MODE_RESIZEABLE_AND_PIPABLE_DEPRECATED = 3;
    /**
     * Activity does not support resizing, but we are forcing it to be resizeable. Only affects
     * certain pre-N apps where we force them to be resizeable.
     * @hide
     */
    public static final int RESIZE_MODE_FORCE_RESIZEABLE = 4;
    /**
     * Activity does not support resizing, but we are forcing it to be resizeable as long
     * as the size remains landscape.
     * @hide
     */
    public static final int RESIZE_MODE_FORCE_RESIZABLE_LANDSCAPE_ONLY = 5;
    /**
     * Activity does not support resizing, but we are forcing it to be resizeable as long
     * as the size remains portrait.
     * @hide
     */
    public static final int RESIZE_MODE_FORCE_RESIZABLE_PORTRAIT_ONLY = 6;
    /**
     * Activity does not support resizing, but we are forcing it to be resizeable as long
     * as the bounds remain in the same orientation as they are.
     * @hide
     */
    public static final int RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION = 7;

    public static final int ROTATION_ANIMATION_UNSPECIFIED = -1;

    int theme;
    int uiOptions;

    @Nullable
    private String targetActivity;

    @Nullable
    private String parentActivityName;
    @Nullable
    String taskAffinity;
    int privateFlags;
    @Nullable
    private String permission;

    int launchMode;
    int documentLaunchMode;
    int maxRecents;
    int configChanges;
    int softInputMode;
    int persistableMode;
    int lockTaskLaunchMode;

    int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    int resizeMode = RESIZE_MODE_RESIZEABLE;

    @Nullable
    private Float maxAspectRatio;

    @Nullable
    private Float minAspectRatio;

    private boolean supportsSizeChanges;

    @Nullable
    String requestedVrComponent;
    int rotationAnimation = -1;
    int colorMode;

    @Nullable
    ActivityInfo.WindowLayout windowLayout;

    public ParsedActivity(ParsedActivity other) {
        super(other);
        this.theme = other.theme;
        this.uiOptions = other.uiOptions;
        this.targetActivity = other.targetActivity;
        this.parentActivityName = other.parentActivityName;
        this.taskAffinity = other.taskAffinity;
        this.privateFlags = other.privateFlags;
        this.permission = other.permission;
        this.launchMode = other.launchMode;
        this.documentLaunchMode = other.documentLaunchMode;
        this.maxRecents = other.maxRecents;
        this.configChanges = other.configChanges;
        this.softInputMode = other.softInputMode;
        this.persistableMode = other.persistableMode;
        this.lockTaskLaunchMode = other.lockTaskLaunchMode;
        this.screenOrientation = other.screenOrientation;
        this.resizeMode = other.resizeMode;
        this.maxAspectRatio = other.maxAspectRatio;
        this.minAspectRatio = other.minAspectRatio;
        this.supportsSizeChanges = other.supportsSizeChanges;
        this.requestedVrComponent = other.requestedVrComponent;
        this.rotationAnimation = other.rotationAnimation;
        this.colorMode = other.colorMode;
        this.windowLayout = other.windowLayout;
    }

    /**
     * Generate activity object that forwards user to App Details page automatically.
     * This activity should be invisible to user and user should not know or see it.
     */
    public static ParsedActivity makeAppDetailsActivity(String packageName, String processName,
            int uiOptions, String taskAffinity, boolean hardwareAccelerated) {
        ParsedActivity activity = new ParsedActivity();
        activity.setPackageName(packageName);
        activity.theme = android.R.style.Theme_NoDisplay;
        activity.exported = true;
        activity.setName("android.app.AppDetailsActivity");
        activity.setProcessName(processName);
        activity.uiOptions = uiOptions;
        activity.taskAffinity = taskAffinity;
        activity.launchMode = ActivityInfo.LAUNCH_MULTIPLE;
        activity.documentLaunchMode = ActivityInfo.DOCUMENT_LAUNCH_NONE;
        activity.maxRecents = ActivityTaskManager.getDefaultAppRecentsLimitStatic();
        activity.configChanges = ParsedActivityUtils.getActivityConfigChanges(0, 0);
        activity.softInputMode = 0;
        activity.persistableMode = ActivityInfo.PERSIST_NEVER;
        activity.screenOrientation = SCREEN_ORIENTATION_UNSPECIFIED;
        activity.resizeMode = RESIZE_MODE_FORCE_RESIZEABLE;
        activity.lockTaskLaunchMode = 0;
        activity.setDirectBootAware(false);
        activity.rotationAnimation = ROTATION_ANIMATION_UNSPECIFIED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.colorMode = ActivityInfo.COLOR_MODE_DEFAULT;
        }
        if (hardwareAccelerated) {
            activity.setFlags(activity.getFlags() | ActivityInfo.FLAG_HARDWARE_ACCELERATED);
        }
        return activity;
    }

    static ParsedActivity makeAlias(String targetActivityName, ParsedActivity target) {
        ParsedActivity alias = new ParsedActivity();
        alias.setPackageName(target.getPackageName());
        alias.setTargetActivity(targetActivityName);
        alias.configChanges = target.configChanges;
        alias.flags = target.flags;
        alias.privateFlags = target.privateFlags;
        alias.icon = target.icon;
        alias.logo = target.logo;
        alias.banner = target.banner;
        alias.labelRes = target.labelRes;
        alias.launchMode = target.launchMode;
        alias.lockTaskLaunchMode = target.lockTaskLaunchMode;
        alias.documentLaunchMode = target.documentLaunchMode;
        alias.descriptionRes = target.descriptionRes;
        alias.screenOrientation = target.screenOrientation;
        alias.taskAffinity = target.taskAffinity;
        alias.theme = target.theme;
        alias.softInputMode = target.softInputMode;
        alias.uiOptions = target.uiOptions;
        alias.parentActivityName = target.parentActivityName;
        alias.maxRecents = target.maxRecents;
        alias.windowLayout = target.windowLayout;
        alias.resizeMode = target.resizeMode;
        alias.maxAspectRatio = target.maxAspectRatio;
        alias.minAspectRatio = target.minAspectRatio;
        alias.supportsSizeChanges = target.supportsSizeChanges;
        alias.requestedVrComponent = target.requestedVrComponent;
        alias.directBootAware = target.directBootAware;
        alias.setProcessName(target.getProcessName());
        return alias;

        // Not all attributes from the target ParsedActivity are copied to the alias.
        // Careful when adding an attribute and determine whether or not it should be copied.
//        alias.enabled = target.enabled;
//        alias.exported = target.exported;
//        alias.permission = target.permission;
//        alias.splitName = target.splitName;
//        alias.persistableMode = target.persistableMode;
//        alias.rotationAnimation = target.rotationAnimation;
//        alias.colorMode = target.colorMode;
//        alias.intents.addAll(target.intents);
//        alias.order = target.order;
//        alias.metaData = target.metaData;
    }

    public ParsedActivity setMaxAspectRatio(int resizeMode, float maxAspectRatio) {
        if (resizeMode == RESIZE_MODE_RESIZEABLE
                || resizeMode == RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION) {
            // Resizeable activities can be put in any aspect ratio.
            return this;
        }

        if (maxAspectRatio < 1.0f && maxAspectRatio != 0) {
            // Ignore any value lesser than 1.0.
            return this;
        }

        this.maxAspectRatio = maxAspectRatio;
        return this;
    }

    public ParsedActivity setMinAspectRatio(int resizeMode, float minAspectRatio) {
        if (resizeMode == RESIZE_MODE_RESIZEABLE
                || resizeMode == RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION) {
            // Resizeable activities can be put in any aspect ratio.
            return this;
        }

        if (minAspectRatio < 1.0f && minAspectRatio != 0) {
            // Ignore any value lesser than 1.0.
            return this;
        }

        this.minAspectRatio = minAspectRatio;
        return this;
    }

    public ParsedActivity setSupportsSizeChanges(boolean supportsSizeChanges) {
        this.supportsSizeChanges = supportsSizeChanges;
        return this;
    }

    public ParsedActivity setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public ParsedActivity setResizeMode(int resizeMode) {
        this.resizeMode = resizeMode;
        return this;
    }

    public ParsedActivity setTargetActivity(String targetActivity) {
        this.targetActivity = StringUtils.safeIntern(targetActivity);
        return this;
    }

    public ParsedActivity setParentActivity(String parentActivity) {
        this.parentActivityName = StringUtils.safeIntern(parentActivity);
        return this;
    }

    public ParsedActivity setPermission(String permission) {
        // Empty string must be converted to null
        this.permission = TextUtils.isEmpty(permission) ? null : permission.intern();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Activity{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(getPackageName()).append('#').append(getName());
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
        dest.writeInt(this.theme);
        dest.writeInt(this.uiOptions);
        dest.writeString(this.targetActivity);
        dest.writeString(this.parentActivityName);
        dest.writeString(this.taskAffinity);
        dest.writeInt(this.privateFlags);
        sForInternedString.parcel(this.permission, dest, flags);
        dest.writeInt(this.launchMode);
        dest.writeInt(this.documentLaunchMode);
        dest.writeInt(this.maxRecents);
        dest.writeInt(this.configChanges);
        dest.writeInt(this.softInputMode);
        dest.writeInt(this.persistableMode);
        dest.writeInt(this.lockTaskLaunchMode);
        dest.writeInt(this.screenOrientation);
        dest.writeInt(this.resizeMode);
        dest.writeValue(this.maxAspectRatio);
        dest.writeValue(this.minAspectRatio);
        dest.writeByte((byte) (this.supportsSizeChanges ? 1: 0));
        dest.writeString(this.requestedVrComponent);
        dest.writeInt(this.rotationAnimation);
        dest.writeInt(this.colorMode);
        dest.writeBundle(this.metaData);

        if (windowLayout != null) {
            dest.writeByte((byte) 1);
            windowLayout.writeToParcel(dest);
        } else {
            dest.writeByte((byte) 0);
        }
    }

    public ParsedActivity() {
    }

    protected ParsedActivity(Parcel in) {
        super(in);
        this.theme = in.readInt();
        this.uiOptions = in.readInt();
        this.targetActivity = in.readString();
        this.parentActivityName = in.readString();
        this.taskAffinity = in.readString();
        this.privateFlags = in.readInt();
        this.permission = sForInternedString.unparcel(in);
        this.launchMode = in.readInt();
        this.documentLaunchMode = in.readInt();
        this.maxRecents = in.readInt();
        this.configChanges = in.readInt();
        this.softInputMode = in.readInt();
        this.persistableMode = in.readInt();
        this.lockTaskLaunchMode = in.readInt();
        this.screenOrientation = in.readInt();
        this.resizeMode = in.readInt();
        this.maxAspectRatio = (Float) in.readValue(Float.class.getClassLoader());
        this.minAspectRatio = (Float) in.readValue(Float.class.getClassLoader());
        this.supportsSizeChanges = in.readByte() == 1;
        this.requestedVrComponent = in.readString();
        this.rotationAnimation = in.readInt();
        this.colorMode = in.readInt();
        this.metaData = in.readBundle(getClass().getClassLoader());
        if (in.readByte() == 1) {
            windowLayout = new ActivityInfo.WindowLayout(in);
        }
    }

    public static final Parcelable.Creator<ParsedActivity> CREATOR = new Creator<ParsedActivity>() {
        @Override
        public ParsedActivity createFromParcel(Parcel source) {
            return new ParsedActivity(source);
        }

        @Override
        public ParsedActivity[] newArray(int size) {
            return new ParsedActivity[size];
        }
    };

    public int getTheme() {
        return theme;
    }

    public int getUiOptions() {
        return uiOptions;
    }

    @Nullable
    public String getTargetActivity() {
        return targetActivity;
    }

    @Nullable
    public String getParentActivityName() {
        return parentActivityName;
    }

    @Nullable
    public String getTaskAffinity() {
        return taskAffinity;
    }

    public int getPrivateFlags() {
        return privateFlags;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public int getLaunchMode() {
        return launchMode;
    }

    public int getDocumentLaunchMode() {
        return documentLaunchMode;
    }

    public int getMaxRecents() {
        return maxRecents;
    }

    public int getConfigChanges() {
        return configChanges;
    }

    public int getSoftInputMode() {
        return softInputMode;
    }

    public int getPersistableMode() {
        return persistableMode;
    }

    public int getLockTaskLaunchMode() {
        return lockTaskLaunchMode;
    }

    public int getScreenOrientation() {
        return screenOrientation;
    }

    public int getResizeMode() {
        return resizeMode;
    }

    @Nullable
    public Float getMaxAspectRatio() {
        return maxAspectRatio;
    }

    @Nullable
    public Float getMinAspectRatio() {
        return minAspectRatio;
    }

    public boolean getSupportsSizeChanges() {
        return supportsSizeChanges;
    }

    @Nullable
    public String getRequestedVrComponent() {
        return requestedVrComponent;
    }

    public int getRotationAnimation() {
        return rotationAnimation;
    }

    public int getColorMode() {
        return colorMode;
    }

    @Nullable
    public ActivityInfo.WindowLayout getWindowLayout() {
        return windowLayout;
    }
}