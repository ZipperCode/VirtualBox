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

package android.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.SharedLibraryInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dalvik.system.BaseDexClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

final class IntentReceiverLeaked extends AndroidRuntimeException {
    
    public IntentReceiverLeaked(String msg) {
        super(msg);
    }
}

final class ServiceConnectionLeaked extends AndroidRuntimeException {
    
    public ServiceConnectionLeaked(String msg) {
        super(msg);
    }
}

/**
 * Local state maintained about a currently loaded .apk.
 * @hide
 */
public final class LoadedApk {
    static final String TAG = "LoadedApk";
    static final boolean DEBUG = false;

    
    private final ActivityThread mActivityThread;
    
    final String mPackageName;
    
    private ApplicationInfo mApplicationInfo;
    
    private String mAppDir;
    
    private String mResDir;
    private String[] mLegacyOverlayDirs;
    private String[] mOverlayPaths;
    
    private String mDataDir;
    //(maxTargetSdk = Build.VERSION_CODES.R, trackingBug = 170729553)
    private String mLibDir;
    //(maxTargetSdk = Build.VERSION_CODES.P, trackingBug = 115609023)
    private File mDataDirFile;
    private File mDeviceProtectedDataDirFile;
    private File mCredentialProtectedDataDirFile;
    
    private final ClassLoader mBaseClassLoader;
    private ClassLoader mDefaultClassLoader;
    private final boolean mSecurityViolation;
    private final boolean mIncludeCode;
    private final boolean mRegisterPackage;
    
    //private final DisplayAdjustments mDisplayAdjustments = new DisplayAdjustments();
    /** WARNING: This may change. Don't hold external references to it. */
    
    Resources mResources;
    
    private ClassLoader mClassLoader;
    
    private Application mApplication;

    private String[] mSplitNames;
    private String[] mSplitAppDirs;
    
    private String[] mSplitResDirs;
    private String[] mSplitClassLoaderNames;

    private AppComponentFactory mAppComponentFactory;
    private final Object mLock = new Object();

    Application getApplication() {
        return mApplication;
    }

    /**
     * Create information about a new .apk
     *
     * NOTE: This constructor is called with ActivityThread's lock held,
     * so MUST NOT call back out to the activity manager.
     */
    public LoadedApk(ActivityThread activityThread, ApplicationInfo aInfo,
            CompatibilityInfo compatInfo, ClassLoader baseLoader,
            boolean securityViolation, boolean includeCode, boolean registerPackage) {
        throw new RuntimeException("Stub!");
    }

    private static ApplicationInfo adjustNativeLibraryPaths(ApplicationInfo info) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Create information about the system package.
     * Must call {@link #installSystemApplicationInfo} later.
     */
    LoadedApk(ActivityThread activityThread) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Sets application info about the system package.
     */
    void installSystemApplicationInfo(ApplicationInfo info, ClassLoader classLoader) {
        throw new RuntimeException("Stub!");
    }

    private AppComponentFactory createAppFactory(ApplicationInfo appInfo, ClassLoader cl) {
        throw new RuntimeException("Stub!");
    }

    public AppComponentFactory getAppFactory() {
        return mAppComponentFactory;
    }

    
    public String getPackageName() {
        return mPackageName;
    }

    
    public ApplicationInfo getApplicationInfo() {
        return mApplicationInfo;
    }

    public int getTargetSdkVersion() {
        return mApplicationInfo.targetSdkVersion;
    }

    public boolean isSecurityViolation() {
        return mSecurityViolation;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        throw new RuntimeException("Stub!");
    }

    public void setCompatibilityInfo(CompatibilityInfo compatInfo) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Gets the array of shared libraries that are listed as
     * used by the given package.
     *
     * @param packageName the name of the package (note: not its
     * file name)
     * @return null-ok; the array of shared libraries, each one
     * a fully-qualified path
     */
    private static String[] getLibrariesFor(String packageName) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Update the ApplicationInfo for an app. If oldPaths is null, all the paths are considered
     * new.
     * @param aInfo The new ApplicationInfo to use for this LoadedApk
     * @param oldPaths The code paths for the old ApplicationInfo object. null means no paths can
     *                 be reused.
     */
    public void updateApplicationInfo(@NonNull ApplicationInfo aInfo,
            @Nullable List<String> oldPaths) {
        throw new RuntimeException("Stub!");
    }

    private void setApplicationInfo(ApplicationInfo aInfo) {
        throw new RuntimeException("Stub!");
    }

    public static void makePaths(ActivityThread activityThread,
                                 ApplicationInfo aInfo,
                                 List<String> outZipPaths) {
        makePaths(activityThread, false, aInfo, outZipPaths, null);
    }

    private static void appendSharedLibrariesLibPathsIfNeeded(
            List<SharedLibraryInfo> sharedLibraries, ApplicationInfo aInfo,
            Set<String> outSeenPaths,
            List<String> outLibPaths) {
        throw new RuntimeException("Stub!");
    }

    public static void makePaths(ActivityThread activityThread,
                                 boolean isBundledApp,
                                 ApplicationInfo aInfo,
                                 List<String> outZipPaths,
                                 List<String> outLibPaths) {
        throw new RuntimeException("Stub!");
    }

    /**
     * This method appends a path to the appropriate native library folder of a
     * library if this library is hosted in an APK. This allows support for native
     * shared libraries. The library API is determined based on the application
     * ABI.
     *
     * @param path Path to the library.
     * @param applicationInfo The application depending on the library.
     * @param outLibPaths List to which to add the native lib path if needed.
     */
    private static void appendApkLibPathIfNeeded(@NonNull String path,
            @NonNull ApplicationInfo applicationInfo, @Nullable List<String> outLibPaths) {
        throw new RuntimeException("Stub!");
    }


    ClassLoader getSplitClassLoader(String splitName) throws NameNotFoundException {
        throw new RuntimeException("Stub!");
    }

    String[] getSplitPaths(String splitName) throws NameNotFoundException {
        throw new RuntimeException("Stub!");
    }

    /**
     * Create a class loader for the {@code sharedLibrary}. Shared libraries are canonicalized,
     * so if we already created a class loader with that shared library, we return it.
     *
     * Implementation notes: the canonicalization of shared libraries is something dex2oat
     * also does.
     */
    ClassLoader createSharedLibraryLoader(SharedLibraryInfo sharedLibrary,
            boolean isBundledApp, String librarySearchPath, String libraryPermittedPath) {
        throw new RuntimeException("Stub!");
    }
    
    public ClassLoader getClassLoader() {
        throw new RuntimeException("Stub!");
    }

    public String getAppDir() {
        return mAppDir;
    }

    public String getLibDir() {
        return mLibDir;
    }

    
    public String getResDir() {
        return mResDir;
    }

    public String[] getSplitAppDirs() {
        return mSplitAppDirs;
    }

    
    public String[] getSplitResDirs() {
        return mSplitResDirs;
    }


    public String[] getOverlayDirs() {
        return mLegacyOverlayDirs;
    }

    public String[] getOverlayPaths() {
        return mOverlayPaths;
    }

    public String getDataDir() {
        return mDataDir;
    }

    
    public File getDataDirFile() {
        return mDataDirFile;
    }

    public File getDeviceProtectedDataDirFile() {
        return mDeviceProtectedDataDirFile;
    }

    public File getCredentialProtectedDataDirFile() {
        return mCredentialProtectedDataDirFile;
    }

    
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    
    public Resources getResources() {
        throw new RuntimeException("Stub!");
    }

    
    public Application makeApplication(boolean forceDefaultAppClass,
            Instrumentation instrumentation) {
        throw new RuntimeException("Stub!");
    }

    public void removeContextRegistrations(Context context,
            String who, String what) {
        throw new RuntimeException("Stub!");
    }
}
