// IVmPackageManagerService.aidl
package com.virtual.box.core.server.pm;

import java.util.List;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ParceledListSlice;

import com.virtual.box.core.server.pm.IVmPackageObserver;
import com.virtual.box.core.server.pm.entity.VmPackageResult;
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption;
import com.virtual.box.core.server.pm.entity.VmInstalledPackageInfo;


interface IVmPackageManagerService {

    void registerPackageObserver(in IVmPackageObserver observer);

    void unregisterPackageObserver(in IVmPackageObserver observer);

    VmPackageResult installPackageAsUser(in VmPackageInstallOption installOptions, int userId);

    int installPackageAsUserAsync(in VmPackageInstallOption installOptions, int userId);

    VmPackageResult uninstallPackageAsUser(in String packageName, int userId);

    VmPackageResult uninstallPackageAsAppData(in String packageName, in String appDataId, int userId);

    boolean isInstalled(in String packageName, int userId);

    List<VmInstalledPackageInfo> getVmInstalledPackageInfos(int flag, int userId);

    VmInstalledPackageInfo getVmInstalledPackageInfo(in String packageName, int flags, int userId);

    PackageInfo getPackageInfo(in String packageName, int flags, int userId);

    ApplicationInfo getApplicationInfo(in String packageName, int flags, int userId);

    ActivityInfo getActivityInfo(in ComponentName componentName, int flags, int userId);

    ActivityInfo getReceiverInfo(in ComponentName componentName, int flags, int userId);

    ServiceInfo getServiceInfo(in ComponentName componentName, int flags, int userId);

    ProviderInfo getProviderInfo(in ComponentName componentName, int flags, int userId);

    ResolveInfo resolveIntent(in Intent intent, in String resolvedType, int flags, int userId);

    ResolveInfo findPersistentPreferredActivity(in Intent intent, int userId);

    ParceledListSlice queryIntentActivities(in Intent intent, in String resolvedType, int flags, int userId);

    ParceledListSlice queryIntentActivityOptions(in ComponentName componentName, in Intent[] specifics,
        in String[] specificTypes,in Intent intent, in String resolvedType, int flags, int userId);

    ParceledListSlice queryIntentReceivers(in Intent intent, in String resolvedType, int flags, int userId);

    ResolveInfo resolveService(in Intent intent, in String resolvedType,int flags, int userId);

    ParceledListSlice queryIntentServices(in Intent intent, in String resolvedType, int flags, int userId);

    ParceledListSlice queryIntentContentProviders(in Intent intent, in String resolvedType, int flags, int userId);

    ProviderInfo resolveContentProvider(in String name, int flags, int userId);

    InstrumentationInfo getInstrumentationInfo(in ComponentName className, int flags, int userId);

    ParceledListSlice queryInstrumentation(String targetPackage, int flags, int userId);

    ResolveInfo resolveActivity(in Intent intent, int flags, String resolvedType, int userId);

    ParceledListSlice queryContentProviders(String processName, int uid, int flags, String metaDataKey);

}