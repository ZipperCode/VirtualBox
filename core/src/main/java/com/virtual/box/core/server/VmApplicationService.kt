package com.virtual.box.core.server

import android.app.ActivityThread
import android.os.IBinder
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.reflect.android.app.HActivityThread

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
internal object VmApplicationService: IVmApplicationService.Stub() {
    override fun getSystemApplicationThread(): IBinder {
        return HActivityThread.getApplicationThread.call( ActivityThread.currentActivityThread())
    }

    override fun getVmMainApplication(): IBinder {
        TODO("Not yet implemented")
    }

}