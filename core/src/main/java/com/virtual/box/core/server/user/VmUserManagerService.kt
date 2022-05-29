package com.virtual.box.core.server.user

import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
object VmUserManagerService : IVmUserManagerService.Stub() {
    private val logger = Logger.getLogger(L.VM_TAG,"VmUserManagerService")

    private val userInfoRepo: VmUserInfoRepo = VmUserInfoRepo()

    fun exists(userId: Int): Boolean{
        return userInfoRepo.exists(userId)
    }

    override fun checkOrCreateUser(userId: Int) {
        if (userInfoRepo.exists(userId)){
            return
        }
        userInfoRepo.createUser(userId)
    }

    fun deleteUserIfExists(userId: Int){
      if (userInfoRepo.exists(userId)){
          userInfoRepo.deleteUser(userId)
      }
    }


}