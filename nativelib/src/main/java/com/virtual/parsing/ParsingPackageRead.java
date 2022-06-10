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

package com.virtual.parsing;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager.Property;
import android.content.pm.PackageParser;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArraySet;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.parsing.component.ParsedActivity;
import com.virtual.parsing.component.ParsedAttribution;
import com.virtual.parsing.component.ParsedInstrumentation;
import com.virtual.parsing.component.ParsedIntentInfo;
import com.virtual.parsing.component.ParsedPermission;
import com.virtual.parsing.component.ParsedPermissionGroup;
import com.virtual.parsing.component.ParsedProcess;
import com.virtual.parsing.component.ParsedProvider;
import com.virtual.parsing.component.ParsedService;
import com.virtual.parsing.component.ParsedUsesPermission;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Everything written by { ParsingPackage} and readable back.
 *
 * @hide
 */
@SuppressWarnings("UnusedReturnValue")
public interface ParsingPackageRead extends Parcelable {

    /**
     *  ActivityInfo
     *  PackageInfo#activities
     */
    @NonNull
    List<ParsedActivity> getActivities();

    /**
     * The names of packages to adopt ownership of permissions from, parsed under
     * { ParsingPackageUtils#TAG_ADOPT_PERMISSIONS}.
     *  R.styleable#AndroidManifestOriginalPackage_name
     */
    @NonNull
    List<String> getAdoptPermissions();

    /**
     *  PackageInfo#configPreferences
     *  R.styleable#AndroidManifestUsesConfiguration
     */
    @NonNull
    List<ConfigurationInfo> getConfigPreferences();

    @NonNull
    List<ParsedAttribution> getAttributions();

    /**
     *  PackageInfo#featureGroups
     *  R.styleable#AndroidManifestUsesFeature
     */
    @NonNull
    List<FeatureGroupInfo> getFeatureGroups();

    /**
     * Permissions requested but not in the manifest. These may have been split or migrated from
     * previous versions/definitions.
     */
    @NonNull
    List<String> getImplicitPermissions();

    /**
     *  android.content.pm.InstrumentationInfo
     *  PackageInfo#instrumentation
     */
    @NonNull
    List<ParsedInstrumentation> getInstrumentations();

    /**
     * For use with { com.android.server.pm.KeySetManagerService}. Parsed in
     * { ParsingPackageUtils#TAG_KEY_SETS}.
     *  R.styleable#AndroidManifestKeySet
     *  R.styleable#AndroidManifestPublicKey
     */
    @NonNull
    Map<String, ArraySet<PublicKey>> getKeySetMapping();

    /**
     * Library names this package is declared as, for use by other packages with "uses-library".
     *  R.styleable#AndroidManifestLibrary
     */
    @NonNull
    List<String> getLibraryNames();

    /**
     * For system use to migrate from an old package name to a new one, moving over data
     * if available.
     *  R.styleable#AndroidManifestOriginalPackage}
     */
    @NonNull
    List<String> getOriginalPackages();

    /**
     * Map of overlayable name to actor name.
     */
    @NonNull
    Map<String, String> getOverlayables();

    /**
     *  android.content.pm.PermissionInfo
     *  PackageInfo#permissions
     */
    @NonNull
    List<ParsedPermission> getPermissions();

    /**
     *  android.content.pm.PermissionGroupInfo
     */
    @NonNull
    List<ParsedPermissionGroup> getPermissionGroups();

    /**
     * Used to determine the default preferred handler of an { Intent}.
     *
     * Map of component className to intent info inside that component.
     * TODO(b/135203078): Is this actually used/working?
     */
    @NonNull
    List<Pair<String, ParsedIntentInfo>> getPreferredActivityFilters();

    /**
     * System protected broadcasts.
     *  R.styleable#AndroidManifestProtectedBroadcast
     */
    @NonNull
    List<String> getProtectedBroadcasts();

    /**
     *  android.content.pm.ProviderInfo
     *  PackageInfo#providers
     */
    @NonNull
    List<ParsedProvider> getProviders();

    /**
     *  android.content.pm.ProcessInfo
     */
    @NonNull
    Map<String, ParsedProcess> getProcesses();

    /**
     * Since they share several attributes, receivers are parsed as { ParsedActivity}, even
     * though they represent different functionality.
     * TODO(b/135203078): Reconsider this and maybe make ParsedReceiver so it's not so confusing
     *  ActivityInfo
     *  PackageInfo#receivers
     */
    @NonNull
    List<ParsedActivity> getReceivers();

    /**
     *  PackageInfo#reqFeatures
     *  R.styleable#AndroidManifestUsesFeature
     */
    @NonNull
    List<FeatureInfo> getReqFeatures();

    /**
     * @deprecated consider migrating to { #getUsesPermissions} which has
     *             more parsed details, such as flags
     */
    @NonNull
    @Deprecated
    List<String> getRequestedPermissions();

    /**
     * All the permissions declared. This is an effective set, and may include permissions
     * transformed from split/migrated permissions from previous versions, so may not be exactly
     * what the package declares in its manifest.
     *  PackageInfo#requestedPermissions
     *  R.styleable#AndroidManifestUsesPermission
     */
    @NonNull
    List<ParsedUsesPermission> getUsesPermissions();

    /**
     * Returns the properties set on the application
     */
    @NonNull
    Map<String, Property> getProperties();

    /**
     * Whether or not the app requested explicitly resizeable Activities.
     * A null value means nothing was explicitly requested.
     */
    @Nullable
    Boolean getResizeableActivity();

    /**
     *  ServiceInfo
     *  PackageInfo#services
     */
    @NonNull
    List<ParsedService> getServices();

    /**  R.styleable#AndroidManifestUsesLibrary */
    @NonNull
    List<String> getUsesLibraries();

    /**
     * Like { #getUsesLibraries()}, but marked optional by setting
     * { R.styleable#AndroidManifestUsesLibrary_required} to false . Application is expected
     * to handle absence manually.
     *  R.styleable#AndroidManifestUsesLibrary
     */
    @NonNull
    List<String> getUsesOptionalLibraries();

    /**  R.styleabele#AndroidManifestUsesNativeLibrary */
    @NonNull
    List<String> getUsesNativeLibraries();

    /**
     * Like { #getUsesNativeLibraries()}, but marked optional by setting
     * { R.styleable#AndroidManifestUsesNativeLibrary_required} to false . Application is
     * expected to handle absence manually.
     *  R.styleable#AndroidManifestUsesNativeLibrary
     */
    @NonNull
    List<String> getUsesOptionalNativeLibraries();

    /**
     * TODO(b/135203078): Move static library stuff to an inner data class
     *  R.styleable#AndroidManifestUsesStaticLibrary
     */
    @NonNull
    List<String> getUsesStaticLibraries();

    /**  R.styleable#AndroidManifestUsesStaticLibrary_certDigest */
    @Nullable
    String[][] getUsesStaticLibrariesCertDigests();

    /**  R.styleable#AndroidManifestUsesStaticLibrary_version */
    @Nullable
    long[] getUsesStaticLibrariesVersions();

    /**
     * Intents that this package may query or require and thus requires visibility into.
     *  R.styleable#AndroidManifestQueriesIntent
     */
    @NonNull
    List<Intent> getQueriesIntents();

    /**
     * Other packages that this package may query or require and thus requires visibility into.
     *  R.styleable#AndroidManifestQueriesPackage
     */
    @NonNull
    List<String> getQueriesPackages();

    /**
     * Authorities that this package may query or require and thus requires visibility into.
     *  R.styleable#AndroidManifestQueriesProvider
     */
    @NonNull
    Set<String> getQueriesProviders();

    /**
     * We store the application meta-data independently to avoid multiple unwanted references
     * TODO(b/135203078): What does this comment mean?
     * TODO(b/135203078): Make all the Bundles immutable (and non-null by shared empty reference?)
     */
    @Nullable
    Bundle getMetaData();

    /**  R.styleable#AndroidManifestApplication_forceQueryable */
    boolean isForceQueryable();

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
     *  ApplicationInfo#permission
     *  R.styleable#AndroidManifestApplication_permission
     */
    @Nullable
    String getPermission();

    /**
     *  ApplicationInfo#processName
     *  R.styleable#AndroidManifestApplication_process
     */
    @NonNull
    String getProcessName();

    /**
     *  PackageInfo#sharedUserId
     *  R.styleable#AndroidManifest_sharedUserId
     */
    @Deprecated
    @Nullable
    String getSharedUserId();

    /**  R.styleable#AndroidManifestStaticLibrary_name */
    @Nullable
    String getStaticSharedLibName();

    /**
     *  ApplicationInfo#taskAffinity
     *  R.styleable#AndroidManifestApplication_taskAffinity
     */
    @Nullable
    String getTaskAffinity();

    /**
     *  ApplicationInfo#targetSdkVersion
     *  R.styleable#AndroidManifestUsesSdk_targetSdkVersion
     */
    int getTargetSdkVersion();

    /**
     *  ApplicationInfo#uiOptions
     *  R.styleable#AndroidManifestApplication_uiOptions
     */
    int getUiOptions();

    boolean isCrossProfile();

    boolean isResizeableActivityViaSdkVersion();

    /**  ApplicationInfo#FLAG_HARDWARE_ACCELERATED */
    boolean isBaseHardwareAccelerated();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_resizeable
     *  ApplicationInfo#FLAG_RESIZEABLE_FOR_SCREENS
     */
    boolean isResizeable();

    /**  ApplicationInfo#PRIVATE_FLAG_ALLOW_AUDIO_PLAYBACK_CAPTURE */
    boolean isAllowAudioPlaybackCapture();

    /**  ApplicationInfo#FLAG_ALLOW_BACKUP */
    boolean isAllowBackup();

    /**  ApplicationInfo#FLAG_ALLOW_CLEAR_USER_DATA */
    boolean isAllowClearUserData();

    /**  ApplicationInfo#PRIVATE_FLAG_ALLOW_CLEAR_USER_DATA_ON_FAILED_RESTORE */
    boolean isAllowClearUserDataOnFailedRestore();

    /**  ApplicationInfo#FLAG_ALLOW_TASK_REPARENTING */
    boolean isAllowTaskReparenting();

    /**
     *  ApplicationInfo#PRIVATE_FLAG_IS_RESOURCE_OVERLAY
     *  ApplicationInfo#isResourceOverlay()
     */
    boolean isOverlay();

    /**  ApplicationInfo#PRIVATE_FLAG_BACKUP_IN_FOREGROUND */
    boolean isBackupInForeground();

    /**  ApplicationInfo#PRIVATE_FLAG_CANT_SAVE_STATE */
    boolean isCantSaveState();

    /**  ApplicationInfo#FLAG_DEBUGGABLE */
    boolean isDebuggable();

    /**  ApplicationInfo#PRIVATE_FLAG_DEFAULT_TO_DEVICE_PROTECTED_STORAGE */
    boolean isDefaultToDeviceProtectedStorage();

    /**  ApplicationInfo#PRIVATE_FLAG_DIRECT_BOOT_AWARE */
    boolean isDirectBootAware();

    /**  ApplicationInfo#FLAG_EXTERNAL_STORAGE */
    boolean isExternalStorage();

    /**  ApplicationInfo#FLAG_EXTRACT_NATIVE_LIBS */
    boolean isExtractNativeLibs();

    /**  ApplicationInfo#FLAG_FULL_BACKUP_ONLY */
    boolean isFullBackupOnly();

    /**  ApplicationInfo#FLAG_HAS_CODE */
    boolean isHasCode();

    /**  ApplicationInfo#PRIVATE_FLAG_HAS_FRAGILE_USER_DATA */
    boolean isHasFragileUserData();

    /**  ApplicationInfo#FLAG_IS_GAME */
    @Deprecated
    boolean isGame();

    /**  ApplicationInfo#PRIVATE_FLAG_ISOLATED_SPLIT_LOADING */
    boolean isIsolatedSplitLoading();

    /**  ApplicationInfo#FLAG_KILL_AFTER_RESTORE */
    boolean isKillAfterRestore();

    /**  ApplicationInfo#FLAG_LARGE_HEAP */
    boolean isLargeHeap();

    /**  ApplicationInfo#FLAG_MULTIARCH */
    boolean isMultiArch();

    /**  ApplicationInfo#PRIVATE_FLAG_PARTIALLY_DIRECT_BOOT_AWARE */
    boolean isPartiallyDirectBootAware();

    /**  ApplicationInfo#FLAG_PERSISTENT */
    boolean isPersistent();

    /**  ApplicationInfo#PRIVATE_FLAG_PROFILEABLE_BY_SHELL */
    boolean isProfileableByShell();

    /**  ApplicationInfo#PRIVATE_FLAG_EXT_PROFILEABLE */
    boolean isProfileable();

    /**  ApplicationInfo#PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE */
    boolean isRequestLegacyExternalStorage();

    /**  ApplicationInfo#FLAG_RESTORE_ANY_VERSION */
    boolean isRestoreAnyVersion();

    // ParsingPackageRead setSplitHasCode(int splitIndex, boolean splitHasCode);

    /** Flags of any split APKs; ordered by parsed splitName */
    @Nullable
    int[] getSplitFlags();

    /**  ApplicationInfo#splitSourceDirs */
    @Nullable
    String[] getSplitCodePaths();

    /**  ApplicationInfo#splitDependencies */
    @Nullable
    SparseArray<int[]> getSplitDependencies();

    /**
     *  ApplicationInfo#splitNames
     *  PackageInfo#splitNames
     */
    @Nullable
    String[] getSplitNames();

    /**  PackageInfo#splitRevisionCodes */
    int[] getSplitRevisionCodes();

    /**  ApplicationInfo#PRIVATE_FLAG_STATIC_SHARED_LIBRARY */
    boolean isStaticSharedLibrary();

    /**  ApplicationInfo#FLAG_SUPPORTS_RTL */
    boolean isSupportsRtl();

    /**  ApplicationInfo#FLAG_TEST_ONLY */
    boolean isTestOnly();

    /**  ApplicationInfo#PRIVATE_FLAG_USE_EMBEDDED_DEX */
    boolean isUseEmbeddedDex();

    /**  ApplicationInfo#FLAG_USES_CLEARTEXT_TRAFFIC */
    boolean isUsesCleartextTraffic();

    /**  ApplicationInfo#PRIVATE_FLAG_USES_NON_SDK_API */
    boolean isUsesNonSdkApi();

    /**
     * Set if the any of components are visible to instant applications.
     *  R.styleable#AndroidManifestActivity_visibleToInstantApps
     *  R.styleable#AndroidManifestProvider_visibleToInstantApps
     *  R.styleable#AndroidManifestService_visibleToInstantApps
     */
    boolean isVisibleToInstantApps();

    /**  ApplicationInfo#FLAG_VM_SAFE_MODE */
    boolean isVmSafeMode();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_anyDensity
     *  ApplicationInfo#FLAG_SUPPORTS_SCREEN_DENSITIES
     */
    boolean isAnyDensity();

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

    String getPackageName();

    /** Path of base APK */
    String getBaseApkPath();

    /**
     * Path where this package was found on disk. For monolithic packages
     * this is path to single base APK file; for cluster packages this is
     * path to the cluster directory.
     */
    @NonNull
    String getPath();

    /**
     *  ApplicationInfo#compatibleWidthLimitDp
     *  R.styleable#AndroidManifestSupportsScreens_compatibleWidthLimitDp
     */
    int getCompatibleWidthLimitDp();

    /**
     *  ApplicationInfo#descriptionRes
     *  R.styleable#AndroidManifestApplication_description
     */
    int getDescriptionRes();

    /**
     *  ApplicationInfo#enabled
     *  R.styleable#AndroidManifestApplication_enabled
     */
    boolean isEnabled();

    /**
     *  ApplicationInfo#fullBackupContent
     *  R.styleable#AndroidManifestApplication_fullBackupContent
     */
    int getFullBackupContent();

    /**
     *  R.styleable#AndroidManifestApplication_dataExtractionRules
     */
    int getDataExtractionRules();

    /**  ApplicationInfo#PRIVATE_FLAG_HAS_DOMAIN_URLS */
    boolean isHasDomainUrls();

    /**
     *  ApplicationInfo#iconRes
     *  R.styleable#AndroidManifestApplication_icon
     */
    int getIconRes();

    /**
     *  ApplicationInfo#installLocation
     *  R.styleable#AndroidManifest_installLocation
     */
    int getInstallLocation();

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
     *  ApplicationInfo#minExtensionVersions
     *  R.styleable#AndroidManifestExtensionSdk
     */
    @Nullable
    SparseIntArray getMinExtensionVersions();

    /**
     *  ApplicationInfo#minSdkVersion
     *  R.styleable#AndroidManifestUsesSdk_minSdkVersion
     */
    int getMinSdkVersion();

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
     *  PackageInfo#overlayCategory
     *  R.styleable#AndroidManifestResourceOverlay_category
     */
    @Nullable
    String getOverlayCategory();

    /**  PackageInfo#mOverlayIsStatic */
    boolean isOverlayIsStatic();

    /**
     *  PackageInfo#overlayPriority
     *  R.styleable#AndroidManifestResourceOverlay_priority
     */
    int getOverlayPriority();

    /**
     *  PackageInfo#overlayTarget
     *  R.styleable#AndroidManifestResourceOverlay_targetPackage
     */
    @Nullable
    String getOverlayTarget();

    /**
     *  PackageInfo#targetOverlayableName
     *  R.styleable#AndroidManifestResourceOverlay_targetName
     */
    @Nullable
    String getOverlayTargetName();

    /**
     * If a system app declares { #getOriginalPackages()}, and the app was previously installed
     * under one of those original package names, the { #getPackageName()} system identifier
     * will be changed to that previously installed name. This will then be non-null, set to the
     * manifest package name, for tracking the package under its true name.
     *
     * TODO(b/135203078): Remove this in favor of checking originalPackages.isEmpty and
     *  getManifestPackageName
     */
    @Nullable
    String getRealPackage();

    /**
     * The required account type without which this application will not function.
     *
     *  PackageInfo#requiredAccountType
     *  R.styleable#AndroidManifestApplication_requiredAccountType
     */
    @Nullable
    String getRequiredAccountType();

    /**
     *  PackageInfo#requiredForAllUsers
     *  R.styleable#AndroidManifestApplication_requiredForAllUsers
     */
    boolean isRequiredForAllUsers();

    /**
     *  ApplicationInfo#requiresSmallestWidthDp
     *  R.styleable#AndroidManifestSupportsScreens_requiresSmallestWidthDp
     */
    int getRequiresSmallestWidthDp();

    /**
     * SHA-512 hash of the only APK that can be used to update a system package.
     *  R.styleable#AndroidManifestRestrictUpdate
     */
    @Nullable
    byte[] getRestrictUpdateHash();

    /**
     * The restricted account authenticator type that is used by this application
     *
     *  PackageInfo#restrictedAccountType
     *  R.styleable#AndroidManifestApplication_restrictedAccountType
     */
    @Nullable
    String getRestrictedAccountType();

    /**
     *  ApplicationInfo#roundIconRes
     *  R.styleable#AndroidManifestApplication_roundIcon
     */
    int getRoundIconRes();

    /**
     *  PackageInfo#sharedUserLabel
     *  R.styleable#AndroidManifest_sharedUserLabel
     */
    @Deprecated
    int getSharedUserLabel();

    /**
     * The signature data of all APKs in this package, which must be exactly the same across the
     * base and splits.
     */
    PackageParser.SigningDetails getSigningDetails();

    /**
     *  ApplicationInfo#splitClassLoaderNames
     *  R.styleable#AndroidManifestApplication_classLoader
     */
    @Nullable
    String[] getSplitClassLoaderNames();

    /**  R.styleable#AndroidManifestStaticLibrary_version */
    long getStaticSharedLibVersion();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_largeScreens
     *  ApplicationInfo#FLAG_SUPPORTS_LARGE_SCREENS
     */
    boolean isSupportsLargeScreens();

    /**
     * If omitted from manifest, returns true.
     *  R.styleable#AndroidManifestSupportsScreens_normalScreens
     *  ApplicationInfo#FLAG_SUPPORTS_NORMAL_SCREENS
     */
    boolean isSupportsNormalScreens();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#DONUT}.
     *  R.styleable#AndroidManifestSupportsScreens_smallScreens
     *  ApplicationInfo#FLAG_SUPPORTS_SMALL_SCREENS
     */
    boolean isSupportsSmallScreens();

    /**
     * If omitted from manifest, returns true if { #getTargetSdkVersion()} >=
     * { android.os.Build.VERSION_CODES#GINGERBREAD}.
     *  R.styleable#AndroidManifestSupportsScreens_xlargeScreens
     *  ApplicationInfo#FLAG_SUPPORTS_XLARGE_SCREENS
     */
    boolean isSupportsExtraLargeScreens();

    /**  ApplicationInfo#PRIVATE_FLAG_ALLOW_NATIVE_HEAP_POINTER_TAGGING */
    boolean isAllowNativeHeapPointerTagging();

    int getAutoRevokePermissions();

    boolean hasPreserveLegacyExternalStorage();

    /**
     *  ApplicationInfo#targetSandboxVersion
     *  R.styleable#AndroidManifest_targetSandboxVersion
     */
    @Deprecated
    int getTargetSandboxVersion();

    /**
     *  ApplicationInfo#theme
     *  R.styleable#AndroidManifestApplication_theme
     */
    int getTheme();

    /**
     * For use with { com.android.server.pm.KeySetManagerService}. Parsed in
     * { ParsingPackageUtils#TAG_KEY_SETS}.
     *  R.styleable#AndroidManifestUpgradeKeySet
     */
    @NonNull
    Set<String> getUpgradeKeySets();

    /**
     * The install time abi override to choose 32bit abi's when multiple abi's
     * are present. This is only meaningfull for multiarch applications.
     * The use32bitAbi attribute is ignored if cpuAbiOverride is also set.
     */
    boolean isUse32BitAbi();

    /**  ApplicationInfo#volumeUuid */
    @Nullable
    String getVolumeUuid();

    /**  ApplicationInfo#zygotePreloadName */
    @Nullable
    String getZygotePreloadName();

    /** Revision code of base APK */
    int getBaseRevisionCode();

    /**  PackageInfo#versionName */
    @Nullable
    String getVersionName();

    /**  PackageInfo#versionCodeMajor */
    @Nullable
    int getVersionCode();

    /**  PackageInfo#versionCodeMajor */
    @Nullable
    int getVersionCodeMajor();

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

    @Nullable
    Set<String> getMimeGroups();

    /**
     *  ApplicationInfo#gwpAsanMode
     *  R.styleable#AndroidManifest_gwpAsanMode
     */
    // @ApplicationInfo.GwpAsanMode
    int getGwpAsanMode();

    /**
     *  ApplicationInfo#memtagMode
     *  R.styleable#AndroidManifest_memtagMode
     */
    // @ApplicationInfo.MemtagMode
    int getMemtagMode();

    /**
     *  ApplicationInfo#nativeHeapZeroInitialized
     *  R.styleable#AndroidManifest_nativeHeapZeroInitialized
     */
    // @ApplicationInfo.NativeHeapZeroInitialized
    int getNativeHeapZeroInitialized();
    @Nullable
    Boolean hasRequestRawExternalStorageAccess();

    /**
     *  ApplicationInfo#hasRequestForegroundServiceExemption()
     *  R.styleable#AndroidManifest_requestForegroundServiceExemption
     */
    boolean hasRequestForegroundServiceExemption();

    // TODO(b/135203078): Hide and enforce going through PackageInfoUtils
    ApplicationInfo toAppInfoWithoutState();

    /**
     * same as toAppInfoWithoutState except without flag computation.
     */
    ApplicationInfo toAppInfoWithoutStateWithoutFlags();

    /**
     * Whether or not the app has said its attribution tags can be made user-visible.
     *  ApplicationInfo#areAttributionsUserVisible()
     */
    boolean areAttributionsUserVisible();
}