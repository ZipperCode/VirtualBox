package com.virtual.box.core.server

import android.os.IBinder
import com.virtual.box.core.VirtualBox.Companion.mainThread
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.reflect.android.app.HActivityThread

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
internal object VmApplicationService: IVmApplicationService.Stub() {
    override fun getSystemApplicationThread(): IBinder {
        return HActivityThread.getApplicationThread.call(mainThread())
    }

    override fun getVmMainApplication(): IBinder {
        TODO("Not yet implemented")
    }

    /**
     * 进程初始化
     */
    fun initAppConfig(appConfig: VmAppConfig){

    }
}