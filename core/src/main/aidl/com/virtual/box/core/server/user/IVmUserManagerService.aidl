// IVmUserManagerService.aidl
package com.virtual.box.core.server.user;

import java.util.List;

// 跨进程用户管理
interface IVmUserManagerService {

    void checkOrCreateUser(int userId);


}
