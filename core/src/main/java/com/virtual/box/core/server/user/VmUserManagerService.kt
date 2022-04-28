package com.virtual.box.core.server.user

import com.virtual.box.base.storage.IParcelDataHandle
import com.virtual.box.base.storage.MapParcelDataHandle
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.server.user.entity.VmUserInfo
import com.virtual.box.core.server.user.entity.VmUserInfoRepo

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
object VmUserManagerService : IVmUserManagerService.Stub() {

    private const val MAP_USER_INFO_KEY = "MAP_USER_INFO_KEY"

    private val configStorageHandle: IParcelDataHandle<VmUserInfoRepo> =
        MapParcelDataHandle(VmFileSystem.mUserInfoConfig.name.replace(".conf", ""), VmUserInfoRepo::class.java)

    private var userConfig: VmUserInfoRepo

    /**
     * 线程锁
     */
    private val lock = Any()


    init {
        userConfig = configStorageHandle.load(MAP_USER_INFO_KEY) ?: VmUserInfoRepo()
    }


    fun exists(userId: Int): Boolean{
        return userConfig[userId] != null
    }

    override fun checkOrCreateUser(userId: Int) {
        if (userConfig.containsKey(userId)){
            return
        }
        val userInfo = VmUserInfo().apply {
            this.userId = userId
            this.createTime = System.currentTimeMillis()
        }
        synchronized(lock){
            userConfig[userId] = userInfo
            configStorageHandle.save(MAP_USER_INFO_KEY, userConfig)
        }
    }



}