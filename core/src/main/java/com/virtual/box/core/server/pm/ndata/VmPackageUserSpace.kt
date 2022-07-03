package com.virtual.box.core.server.pm.ndata

import android.util.SparseArray
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.core.server.pm.entity.VmPackageUserSpaceConfigInfo

class VmPackageUserSpace() {

    val installedPackage = HashMap<Int, VmNewVmPackageSettings>()

    /**
     * 用户存储空间配置
     */
    val userSpaceConfig = HashMap<String, VmNewPackageUserSpaceConfigInfo>()
}