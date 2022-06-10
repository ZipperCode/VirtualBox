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

package com.virtual.parsing.pkg;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Container for fields that are eventually exposed through { ApplicationInfo}.
 *
 * Done to separate the meaningless, re-directed JavaDoc for methods and to separate what's
 * exposed vs not exposed to core.
 *
 * @hide
 */
interface PkgAppInfo {

    /**  ApplicationInfo#PRIVATE_FLAG_CANT_SAVE_STATE */
    boolean isCantSaveState();

    /**
     *  ApplicationInfo#appComponentFactory
     *  R.styleable#AndroidManifestApplication_appComponentFactory
     */
    @Nullable
    String getAppComponentFactory();

    /**
     *  ApplicationInfo#backupAgentName
     *  R.styleable#AndroidManifestApplication_backupAgent
     */
    @Nullable
    String getBackupAgentName();

    /**
     *  ApplicationInfo#banner
     *  R.styleable#AndroidManifestApplication_banner
     */
    int getBanner();

    /**
     *  ApplicationInfo#category
     *  R.styleable#AndroidManifestApplication_appCategory
     */
    int getCategory();

    /**
     *  ApplicationInfo#classLoaderName
     *  R.styleable#AndroidManifestApplication_classLoader
     */
    @Nullable
    String getClassLoaderName();

    /**
     *  ApplicationInfo#className
     *  R.styleable#AndroidManifestApplication_name
     */
    @Nullable
    String getClassName();

    /**
     *  ApplicationInfo#compatibleWidthLimitDp
     *  R.styleable#AndroidManifestSupportsScreens_compatibleWidthLimitDp
     */
    int getCompatibleWidthLimitDp();

    /**
     *  ApplicationInfo#compileSdkVersion
     *  R.styleable#AndroidManifest_compileSdkVersion
     */
    int getCompileSdkVersion();

    /**
     *  ApplicationInfo#compileSdkVersionCodename
     *  R.styleable#AndroidManifest_compileSdkVersionCodename
     */
    @Nullable
    String getCompileSdkVersionCodeName();

    /**
     *  ApplicationInfo#descriptionRes
     *  R.styleable#AndroidManifestApplication_description
     */
    int getDescriptionRes();

    /**
     *  ApplicationInfo#fullBackupContent
     *  R.styleable#AndroidManifestApplication_fullBackupContent
     */
    int getFullBackupContent();

    /**
     *  ApplicationInfo#iconRes
     *  R.styleable#AndroidManifestApplication_icon
     */
    int getIconRes();

    /**
     *  ApplicationInfo#labelRes
     *  R.styleable#AndroidManifestApplication_label
     */
    int getLabelRes();

    /**
     *  ApplicationInfo#largestWidthLimitDp
     *  R.styleable#AndroidManifestSupportsScreens_largestWidthLimitDp
     */
    int getLargestWidthLimitDp();

    /**
     *  ApplicationInfo#logo
     *  R.styleable#AndroidManifestApplication_logo
     */
    int getLogo();

    /**
     *  ApplicationInfo#manageSpaceActivityName
     *  R.styleable#AndroidManifestApplication_manageSpaceActivity
     */
    @Nullable
    String getManageSpaceActivityName();

    /**
     *  ApplicationInfo#maxAspectRatio
     *  R.styleable#AndroidManifestApplication_maxAspectRatio
     */
    float getMaxAspectRatio();

    /**
     *  ApplicationInfo#minAspectRatio
     *  R.styleable#AndroidManifestApplication_minAspectRatio
     */
    float getMinAspectRatio();

    /**
     *  ApplicationInfo#minSdkVersion
     *  R.styleable#AndroidManifestUsesSdk_minSdkVersion
     */
    int getMinSdkVersion();

    /**  ApplicationInfo#nativeLibraryDir */
    @Nullable
    String getNativeLibraryDir();

    /**  ApplicationInfo#nativeLibraryRootDir */
    @Nullable
    String getNativeLibraryRootDir();

    /**
     *  ApplicationInfo#networkSecurityConfigRes
     *  R.styleable#AndroidManifestApplication_networkSecurityConfig
     */
    int getNetworkSecurityConfigRes();

    /**
     * If { R.styleable#AndroidManifestApplication_label} is a string literal, this is it.
     * Otherwise, it's stored as { #getLabelRes()}.
     *  ApplicationInfo#nonLocalizedLabel
     *  R.styleable#AndroidManifestApplication_label
     */
    @Nullable
    CharSequence getNonLocalizedLabel();

    /**
     *  ApplicationInfo#permission
     *  R.styleable#AndroidManifestApplication_permission
     */
    @Nullable
    String getPermission();

    /**
     * TODO(b/135203078): Hide this in the utility, should never be accessed directly
     *  ApplicationInfo#primaryCpuAbi
     */
    @Nullable
    String getPrimaryCpuAbi();

    /**
     *  ApplicationInfo#processName
     *  R.styleable#AndroidManifestApplication_process
     */
    @NonNull
    String getProcessName();

    /**
     *  ApplicationInfo#requiresSmallestWidthDp
     *  R.styleable#AndroidManifestSupportsScreens_requiresSmallestWidthDp
     */
    int getRequiresSmallestWidthDp();

    /**
     *  ApplicationInfo#roundIconRes
     *  R.styleable#AndroidManifestApplication_roundIcon
     */
    int getRoundIconRes();

    /**
     *  ApplicationInfo#areAttributionsUserVisible()
     *  R.styleable#AndroidManifestApplication_attributionsAreUserVisible
     */
    boolean areAttributionsUserVisible();

    /**  ApplicationInfo#seInfo */
    @Nullable
    String getSeInfo();

    /**  ApplicationInfo#seInfoUser */
    @Nullable
    String getSeInfoUser();

    /**  ApplicationInfo#secondaryCpuAbi */
    @Nullable
    String getSecondaryCpuAbi();

    /**  ApplicationInfo#secondaryNativeLibraryDir */
    @Nullable
    String getSecondaryNativeLibraryDir();

    /**
     *  ApplicationInfo#installLocation
     *  R.styleable#AndroidManifest_installLocation
     */
    int getInstallLocation();

    /**
     *  ApplicationInfo#splitClassLoaderNames
     *  R.styleable#AndroidManifestApplication_classLoader
     */
    @Nullable
    String[] getSplitClassLoaderNames();

    /**  ApplicationInfo#splitSourceDirs */
    @Nullable
    String[] getSplitCodePaths();

    /**  ApplicationInfo#splitDependencies */
    @Nullable
    SparseArray<int[]> getSplitDependencies();

    /**
     *  ApplicationInfo#targetSandboxVersion
     *  R.styleable#AndroidManifest_targetSandboxVersion
     */
    @Deprecated
    int getTargetSandboxVersion();

    /**
     *  ApplicationInfo#targetSdkVersion
     *  R.styleable#AndroidManifestUsesSdk_targetSdkVersion
     */
    int getTargetSdkVersion();

    /**
     *  ApplicationInfo#taskAffinity
     *  R.styleable#AndroidManifestApplication_taskAffinity
     */
    @Nullable
    String getTaskAffinity();

    /**
     *  ApplicationInfo#theme
     *  R.styleable#AndroidManifestApplication_theme
     */
    int getTheme();

    /**
     *  ApplicationInfo#uiOptions
     *  R.styleable#AndroidManifestApplication_uiOptions
     */
    int getUiOptions();

    /**  ApplicationInfo#uid */
    int getUid();

    /**  ApplicationInfo#longVersionCode */
    long getLongVersionCode();

    /**  ApplicationInfo#versionCode */
    @Deprecated
    int getVersionCode();

    /**  ApplicationInfo#volumeUuid */
    @Nullable
    String getVolumeUuid();

    /**  ApplicationInfo#zygotePreloadName */
    @Nullable
    String getZygotePreloadName();

    /**  ApplicationInfo#FLAG_HAS_CODE */
    boolean isHasCode();

    /**  ApplicationInfo#FLAG_ALLOW_TASK_REPARENTING */
    boolean isAllowTaskReparenting();

    /**  ApplicationInfo#FLAG_MULTIARCH */
    boolean isMultiArch();

    /**  ApplicationInfo#FLAG_EXTRACT_NATIVE_LIBS */
    boolean isExtractNativeLibs();

    /**  ApplicationInfo#FLAG_DEBUGGABLE */
    boolean isDebuggable();

    /**  ApplicationInfo#FLAG_VM_SAFE_MODE */
    boolean isVmSafeMode();

    /**  ApplicationInfo#FLAG_PERSISTENT */
    boolean isPersistent();

    /**  ApplicationInfo#FLAG_ALLOW_BACKUP */
    boolean isAllowBackup();

    /**  ApplicationInfo#FLAG_TEST_ONLY */
    boolean isTestOnly();

    /**  ApplicationInfo#PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION */
    boolean isResizeableActivityViaSdkVersion();

    /**  ApplicationInfo#PRIVATE_FLAG_HAS_DOMAIN_URLS */
    boolean isHasDomainUrls();

    /**  ApplicationInfo#PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE */
    boolean isRequestLegacyExternalStorage();

    /**  ApplicationInfo#FLAG_HARDWARE_ACCELERATED */
    boolean isBaseHardwareAccelerated();

    /**  ApplicationInfo#PRIVATE_FLAG_DEFAULT_TO_DEVICE_PROTECTED_STORAGE */
    boolean isDefaultToDeviceProtectedStorage();

    /**  ApplicationInfo#PRIVATE_FLAG_DIRECT_BOOT_AWARE */
    boolean isDirectBootAware();

    /**  ApplicationInfo#PRIVATE_FLAG_PARTIALLY_DIRECT_BOOT_AWARE */
    boolean isPartiallyDirectBootAware();

    /**  ApplicationInfo#PRIVATE_FLAG_USE_EMBEDDED_DEX */
    boolean isUseEmbeddedDex();

    /**  ApplicationInfo#FLAG_EXTERNAL_STORAGE */
    boolean isExternalStorage();

    /**  ApplicationInfo#nativeLibraryRootRequiresIsa */
    boolean isNativeLibraryRootRequiresIsa();

    /**  ApplicationInfo#PRIVATE_FLAG_ODM */
    boolean isOdm();

    /**  ApplicationInfo#PRIVATE_FLAG_OEM */
    boolean isOem();

    /**  ApplicationInfo#PRIVATE_FLAG_PRIVILEGED */
    boolean isPrivileged();

    /**  ApplicationInfo#PRIVATE_FLAG_PRODUCT */
    boolean isProduct();

    /**  ApplicationInfo#PRIVATE_FLAG_PROFILEABLE_BY_SHELL */
    boolean isProfileableByShell();

    /**  ApplicationInfo#PRIVATE_FLAG_STATIC_SHARED_LIBRARY */
    boolean isStaticSharedLibrary();

    /**  ApplicationInfo#FLAG_SYSTEM */
    boolean isSystem();

    /**  ApplicationInfo#PRIVATE_FLAG_SYSTEM_EXT */
    boolean isSystemExt();

    /**  ApplicationInfo#PRIVATE_FLAG_VENDOR */
    boolean isVendor();

    /**  ApplicationInfo#PRIVATE_FLAG_ISOLATED_SPLIT_LOADING */
    boolean isIsolatedSplitLoading();

    /**
     *  ApplicationInfo#enabled
     *  R.styleable#AndroidManifestApplication_enabled
     */
    boolean isEnabled();

    /**
     *  ApplicationInfo#PRIVATE_FLAG_IS_RESOURCE_OVERLAY
     *  ApplicationInfo#isResourceOverlay()
     */
    boolean isOverlay();

    /**  ApplicationInfo#PRIVATE_FLAG_USES_NON_SDK_API */
    boolean isUsesNonSdkApi();

    /**  ApplicationInfo#PRIVATE_FLAG_SIGNED_WITH_PLATFORM_KEY */
    boolean isSignedWithPlatformKey();

    /**  ApplicationInfo#FLAG_KILL_AFTER_RESTORE */
    boolean isKillAfterRestore();

    /**  ApplicationInfo#FLAG_RESTORE_ANY_VERSION */
    boolean isRestoreAnyVersion();

    /**  ApplicationInfo#FLAG_FULL_BACKUP_ONLY */
    boolean isFullBackupOnly();

    /**  ApplicationInfo#FLAG_ALLOW_CLEAR_USER_DATA */
    boolean isAllowClearUserData();

    /**  ApplicationInfo#FLAG_LARGE_HEAP */
    boolean isLargeHeap();

    /**  ApplicationInfo#FLAG_USES_CLEARTEXT_TRAFFIC */
    boolean isUsesCleartextTraffic();

    /**  ApplicationInfo#FLAG_SUPPORTS_RTL */
    boolean isSupportsRtl();

    /**  ApplicationInfo#FLAG_IS_GAME */
    @Deprecated
    boolean isGame();

    /**  ApplicationInfo#FLAG_FACTORY_TEST */
    boolean isFactoryTest();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_smallScreens
     *  ApplicationInfo#FLAG_SUPPORTS_SMALL_SCREENS
     */
    boolean isSupportsSmallScreens();

    /**
     * If omitted from manifest, returns true.
     *  R.styleable#AndroidManifestSupportsScreens_normalScreens
     *  ApplicationInfo#FLAG_SUPPORTS_NORMAL_SCREENS
     */
    boolean isSupportsNormalScreens();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_largeScreens
     *  ApplicationInfo#FLAG_SUPPORTS_LARGE_SCREENS
     */
    boolean isSupportsLargeScreens();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#GINGERBREAD}.
     *  R.styleable#AndroidManifestSupportsScreens_xlargeScreens
     *  ApplicationInfo#FLAG_SUPPORTS_XLARGE_SCREENS
     */
    boolean isSupportsExtraLargeScreens();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_resizeable
     *  ApplicationInfo#FLAG_RESIZEABLE_FOR_SCREENS
     */
    boolean isResizeable();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_anyDensity
     *  ApplicationInfo#FLAG_SUPPORTS_SCREEN_DENSITIES
     */
    boolean isAnyDensity();

    /**  ApplicationInfo#PRIVATE_FLAG_BACKUP_IN_FOREGROUND */
    boolean isBackupInForeground();

    /**  ApplicationInfo#PRIVATE_FLAG_ALLOW_CLEAR_USER_DATA_ON_FAILED_RESTORE */
    boolean isAllowClearUserDataOnFailedRestore();

    /**  ApplicationInfo#PRIVATE_FLAG_ALLOW_AUDIO_PLAYBACK_CAPTURE */
    boolean isAllowAudioPlaybackCapture();

    /**  ApplicationInfo#PRIVATE_FLAG_HAS_FRAGILE_USER_DATA */
    boolean isHasFragileUserData();
}