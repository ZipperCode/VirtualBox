package com.virtual.box.core.hook.service

import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import com.virtual.box.core.hook.BaseHookHandle
import com.virtual.box.reflect.android.os.HServiceManager
import java.io.FileDescriptor

abstract class BaseBinderHookHandle : BaseHookHandle, IBinder {

    protected var originBinder: IBinder? = null
        private set

    protected var serviceName: String? = null

    constructor(serviceName: String){
        this.serviceName = serviceName
        originBinder = HServiceManager.getService.call(serviceName)
    }

    constructor(serviceName: String, originBinder: IBinder){
        this.serviceName = serviceName
        this.originBinder = originBinder
    }

    override fun hookInject(target: Any, proxy: Any) {
        serviceName?.let { replaceSystemService(it) }
    }

    override fun getInterfaceDescriptor(): String? {
        return originBinder?.interfaceDescriptor
    }

    override fun pingBinder(): Boolean {
        return originBinder?.pingBinder() ?: false
    }

    override fun isBinderAlive(): Boolean {
        return originBinder?.isBinderAlive() ?: false
    }

    override fun queryLocalInterface(descriptor: String): IInterface? {
        return originBinder?.queryLocalInterface(descriptor)
    }

    override fun dump(fd: FileDescriptor, args: Array<out String>?) {
        originBinder?.dump(fd, args)
    }

    override fun dumpAsync(fd: FileDescriptor, args: Array<out String>?) {
        originBinder?.dumpAsync(fd, args)
    }

    override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        return originBinder?.transact(code, data, reply, flags) ?: false
    }

    override fun linkToDeath(recipient: IBinder.DeathRecipient, flags: Int) {
        originBinder?.linkToDeath(recipient, flags)
    }

    override fun unlinkToDeath(recipient: IBinder.DeathRecipient, flags: Int): Boolean {
        return originBinder?.unlinkToDeath(recipient, flags) ?: false
    }

    protected open fun replaceSystemService(name: String) {
        val services: MutableMap<String, IBinder> = HServiceManager.sCache.get()
        services[name] = this
    }
}