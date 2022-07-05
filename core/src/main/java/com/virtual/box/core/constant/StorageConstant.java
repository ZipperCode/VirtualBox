package com.virtual.box.core.constant;

public interface StorageConstant {

    String VM_PK_INFO = "VmPackageInfoStorage";

    String VM_PK_CONFIG_INFO = "VmPackageConfigStorage";

    String VM_PK_RESOLVER_INFO = "VmPackageResolverStorage";

    String VM_USER_INFO_NAME = "VmUserInfo";

    /**
     * 用户包配置数据
     * 存储安装包key
     * 用户数据key
     * eg:
     *      key: install_pkg_[userId] >  存储安装包的key      [install_pkg_0_com.example.demo, install_pkg_com.example.demo2]
     *      key: app_data_[userId]    >  存储的用户数据key    [app_data_0_com.example.demo, app_data_0_com.example.demo2]
     *
     */
    String VM_USER_PKG_CONFIG_INFO = "VmUserPackageConfigInfoStorage";
    /**
     * 已安装包的配置信息
     * key来自 [VM_USER_PKG_CONFIG_INFO] 中存储的key
     * eg:
     *      key:install_pkg_0_com.example.demo  > VmPackageConfigInfo
     */
    String VM_INSTALL_PKG_CONFIG_INFO = "VmInstalledPackageInfoStorage";
    /**
     * 用户数据的配置信息
     * key来自 [VM_USER_PKG_CONFIG_INFO] 中存储的key
     * key: app_data_[userId]_[pkg]
     * eg:
     *      key:app_data_0_com.example.demo  > VmAppDataConfigInfo
     */
    String VM_APP_DATA_CONFIG_INFO = "VmAppDataConfigInfoStorage";
    /**
     * 存储当前用户下已安装的包解析数据 [VmPackage]
     * eg:
     *      key:resolved_pkg_[userId]
     */
    String VM_RESOLVED_PKG_DATA = "VmResolvedPackageDataStorage";
    /**
     * 存储当前用户下已安装包数据 [PackageInfo]
     * eg:
     *      key:install_pkg_[userId]
     */
    String VM_INSTALL_PKG_DATA = "VmInstallPackageDataStorage";
}
