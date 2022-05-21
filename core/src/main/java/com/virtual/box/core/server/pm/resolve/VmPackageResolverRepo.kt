package com.virtual.box.core.server.pm.resolve

import android.content.Intent
import android.content.pm.PackageParser
import android.content.pm.ResolveInfo
import androidx.annotation.WorkerThread
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.server.pm.data.VmPackageDataSource
import com.virtual.box.core.server.pm.data.VmPackageResolverDataSource
import java.util.*

class VmPackageResolverRepo(
    private val vmPackageConfigDataSource: VmPackageDataSource,
    private val vmPackageResolverDataSource: VmPackageResolverDataSource
) {


    companion object{
        const val PackageResolverInfo = "PackageResolverInfo"
    }
}