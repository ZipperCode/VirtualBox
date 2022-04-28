package com.virtual.box.core.server.pm;

import com.virtual.box.core.server.pm.entity.VmPackageResult;

interface IVmPackageObserver{

    void onPackageResult(in VmPackageResult installResult);

}