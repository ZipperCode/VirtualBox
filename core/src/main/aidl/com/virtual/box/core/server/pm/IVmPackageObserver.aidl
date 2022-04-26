package com.virtual.box.core.server.pm;

import com.virtual.box.core.server.pm.entity.VmPackageInstallResult;

interface IVmPackageObserver{

    void onPackageResult(in VmPackageInstallResult installResult);

}