package com.virtual.box.core.server.user

import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.server.user.entity.VmUserInfo

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
object VmUserManagerService : IVmUserManagerService.Stub() {
    private val logger = Logger.getLogger(L.VM_TAG,"VmUserManagerService")

    private val userInfoRepo: VmUserInfoRepo = VmUserInfoRepo()

    override fun checkOrCreateUser(userId: Int) {
        if (userInfoRepo.exists(userId)){
            return
        }
        userInfoRepo.createUser(userId)
    }

    override fun checkUserExists(userId: Int): Boolean {
        return userInfoRepo.exists(userId)
    }

    override fun getVmUserInfo(userId: Int): VmUserInfo? {
        return userInfoRepo.loadUserInfo(userId)
    }

    override fun loadAllAsUserId(): IntArray {
        val loadUserKeys = userInfoRepo.loadUserKeys()
        return loadUserKeys.toIntArray()
    }

    override fun loadAllUserInfo(): MutableList<VmUserInfo> {
        return userInfoRepo.loadAllUserWithLock().toMutableList()
    }

    override fun deleteUserIfExists(userId: Int){
      if (userInfoRepo.exists(userId)){
          userInfoRepo.deleteUser(userId)
      }
    }


}