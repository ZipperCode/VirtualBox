package com.virtual.box.core.hook.service

import com.virtual.box.core.hook.BaseHookHandle
import com.virtual.box.reflect.android.app.HActivityClient

class IActivityClientControllerHookHandle: BaseHookHandle() {
    override fun getOriginObject(): Any? {
        return HActivityClient.getActivityClientController.call()
    }

    override fun hookInject(target: Any, proxy: Any) {
        HActivityClient.setActivityClientController.call(proxy)
    }

    override fun isHooked(): Boolean {
        return HActivityClient.getActivityClientController.call() == proxyInvocation
    }

}