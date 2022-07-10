// IVmUserManagerService.aidl
package com.virtual.box.core.server.user;

import java.util.List;

import com.virtual.box.core.server.user.entity.VmUserInfo;

// 跨进程用户管理
interface IVmUserManagerService {

    void checkOrCreateUser(int userId);

    boolean checkUserExists(int userId);

    VmUserInfo getVmUserInfo(int userId);

    int[] loadAllAsUserId();

    List<VmUserInfo> loadAllUserInfo();

    void deleteUserIfExists(int userId);
}
