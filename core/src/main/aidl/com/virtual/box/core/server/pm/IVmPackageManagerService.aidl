// IVmPackageManagerService.aidl
package com.virtual.box.core.server.pm;

import java.util.List;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;

import com.virtual.box.core.server.pm.IVmPackageObserver;
import com.virtual.box.core.server.pm.entity.VmPackageInstallResult;
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption;
import com.virtual.box.core.server.pm.entity.VmInstalledPackageInfo;


interface IVmPackageManagerService {

    void registerPackageObserver(in IVmPackageObserver observer);

    void unregisterPackageObserver(in IVmPackageObserver observer);

    VmPackageInstallResult installPackageAsUser(in VmPackageInstallOption installOptions, int userId);

    int installPackageAsUserAsync(in VmPackageInstallOption installOptions, int userId);

    int uninstallPackageAdUser(String packageName, int userId);

    boolean isInstalled(String packageName, int userId);


}