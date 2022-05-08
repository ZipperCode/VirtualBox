/*
 * Copyright (C) 2006 The Android Open Source Project
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

package android.content.res;

import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.translation.Translator;

/**
 * CompatibilityInfo class keeps the information about the screen compatibility mode that the
 * application is running under.
 * 
 *  {@hide} 
 */
public class CompatibilityInfo implements Parcelable {
    /** default compatibility info object for compatible applications */
    public static final CompatibilityInfo DEFAULT_COMPATIBILITY_INFO = new CompatibilityInfo() {
    };

    /**
     * This is the number of pixels we would like to have along the
     * short axis of an app that needs to run on a normal size screen.
     */
    public static final int DEFAULT_NORMAL_SHORT_DIMENSION = 320;

    /**
     * This is the maximum aspect ratio we will allow while keeping
     * applications in a compatible screen size.
     */
    public static final float MAXIMUM_ASPECT_RATIO = (854f/480f);

    /**
     *  A compatibility flags
     */
    private final int mCompatibilityFlags;
    
    /**
     * A flag mask to tell if the application needs scaling (when mApplicationScale != 1.0f)
     * {@see compatibilityFlag}
     */
    private static final int SCALING_REQUIRED = 1; 

    /**
     * Application must always run in compatibility mode?
     */
    private static final int ALWAYS_NEEDS_COMPAT = 2;

    /**
     * Application never should run in compatibility mode?
     */
    private static final int NEVER_NEEDS_COMPAT = 4;

    /**
     * Set if the application needs to run in screen size compatibility mode.
     */
    private static final int NEEDS_SCREEN_COMPAT = 8;

    /**
     * Set if the application needs to run in with compat resources.
     */
    private static final int NEEDS_COMPAT_RES = 16;

    /**
     * Set if the application needs to be forcibly downscaled
     */
    private static final int HAS_OVERRIDE_SCALING = 32;

    /**
     * The effective screen density we have selected for this application.
     */
    public final int applicationDensity;
    
    /**
     * Application's scale.
     */
    public final float applicationScale;

    /**
     * Application's inverted scale.
     */
    public final float applicationInvertedScale;
    

    public CompatibilityInfo(ApplicationInfo appInfo, int screenLayout, int sw,
            boolean forceCompat, float overrideScale) {
        throw new RuntimeException("Stub!");
    }

    private CompatibilityInfo(int compFlags,
            int dens, float scale, float invertedScale) {
        mCompatibilityFlags = compFlags;
        applicationDensity = dens;
        applicationScale = scale;
        applicationInvertedScale = invertedScale;
    }

    private CompatibilityInfo() {
        throw new RuntimeException("Stub!");
    }

    /**
     * @return true if the scaling is required
     */
    
    public boolean isScalingRequired() {
        return (mCompatibilityFlags & (SCALING_REQUIRED | HAS_OVERRIDE_SCALING)) != 0;
    }
    
    
    public boolean supportsScreen() {
        return (mCompatibilityFlags&NEEDS_SCREEN_COMPAT) == 0;
    }
    
    public boolean neverSupportsScreen() {
        return (mCompatibilityFlags&ALWAYS_NEEDS_COMPAT) != 0;
    }

    public boolean alwaysSupportsScreen() {
        return (mCompatibilityFlags&NEVER_NEEDS_COMPAT) != 0;
    }

    public boolean needsCompatResources() {
        return (mCompatibilityFlags&NEEDS_COMPAT_RES) != 0;
    }

    /**
     * Returns the translator which translates the coordinates in compatibility mode.
     * @param params the window's parameter
     */
    
    public Translator getTranslator() {
        throw new RuntimeException("Stub!");
    }


    public void applyToDisplayMetrics(DisplayMetrics inoutDm) {
        throw new RuntimeException("Stub!");
    }

    public void applyToConfiguration(int displayDensity, Configuration inoutConfig) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Compute the frame Rect for applications runs under compatibility mode.
     *
     * @param dm the display metrics used to compute the frame size.
     * @param outDm If non-null the width and height will be set to their scaled values.
     * @return Returns the scaling factor for the window.
     *     (maxTargetSdk = Build.VERSION_CODES.R, trackingBug = 170729553)
     */
    public static float computeCompatibleScaling(DisplayMetrics dm, DisplayMetrics outDm) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCompatibilityFlags);
        dest.writeInt(applicationDensity);
        dest.writeFloat(applicationScale);
        dest.writeFloat(applicationInvertedScale);
    }


    public static final Parcelable.Creator<CompatibilityInfo> CREATOR
            = new Parcelable.Creator<CompatibilityInfo>() {
        @Override
        public CompatibilityInfo createFromParcel(Parcel source) {
            return new CompatibilityInfo(source);
        }

        @Override
        public CompatibilityInfo[] newArray(int size) {
            return new CompatibilityInfo[size];
        }
    };

    private CompatibilityInfo(Parcel source) {
        mCompatibilityFlags = source.readInt();
        applicationDensity = source.readInt();
        applicationScale = source.readFloat();
        applicationInvertedScale = source.readFloat();
    }
}
