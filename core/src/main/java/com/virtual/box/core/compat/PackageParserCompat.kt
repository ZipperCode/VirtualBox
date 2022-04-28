package com.virtual.box.core.compat

import android.content.pm.PackageParser
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.core.server.pm.entity.VmPackageSetting
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import com.virtual.box.reflect.android.content.pm.HPackageParser

/**
 * 安装包解析器适配类
 */
object PackageParserCompat {

    @JvmStatic
    @Throws(Throwable::class)
    fun collectCertificates(parser: PackageParser?, p: PackageParser.Package?, flags: Int) {
        when {
            BuildCompat.isAtLeastPie -> {
                PackageParser.collectCertificates(p, true)
            }
            else -> {
                parser?.collectCertificates(p, 0)
                HPackageParser.collectCertificates.callWithException(p, flags)
            }
        }
    }


}