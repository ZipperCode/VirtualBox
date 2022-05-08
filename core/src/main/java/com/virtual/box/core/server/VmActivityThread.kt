package com.virtual.box.core.server

import android.os.IBinder
import com.virtual.box.core.server.am.IVmActivityThread

/**
 * 虚拟进程的ActivityThread模拟实现
 *
 */
internal object VmActivityThread: IVmActivityThread.Stub() {
    override fun getVmActivityThread(): IBinder {
        TODO("Not yet implemented")
    }
}