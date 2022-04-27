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

    /**
     * 将指定包转换为BPackageSetting
     * @param packageName
     * @param aPackage
     * @return
     */
    fun convertBPackageSettings(aPackage: PackageParser.Package, installOption: VmPackageInstallOption): VmPackageSetting {
        val packageName: String = aPackage.packageName
        // logger.method("系统包配置转化为自定义实现的包配置", "packageName = %s, aPackage = %s", packageName, aPackage)
        val origSettings = VmPackageSetting()
//        origSettings.pkg = BPackage(aPackage)
//        origSettings.pkg!!.mExtras = origSettings
//        origSettings.pkg!!.applicationInfo = PackageManagerCompat
//            .generateApplicationInfo(origSettings.pkg, 0, BPackageUserState.create(), 0)
//        origSettings.installOption = installOption
//        synchronized(mPackages) {
//            val pkgSettings = mPackages[packageName]
//            if (pkgSettings != null) {
//                origSettings.appId = pkgSettings.appId
//                origSettings.userState = pkgSettings.userState
//            } else {
//                val isCreateSuccess = registerAppIdLPw(origSettings)
//                if (!isCreateSuccess){
//                    throw IllegalStateException("创建appId失败")
//                }
//            }
//        }
        return origSettings
    }

}