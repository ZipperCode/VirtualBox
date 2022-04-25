package com.virtual.box.core.hook.service

import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import com.virtual.box.core.hook.BaseHookHandle
import com.virtual.box.reflect.android.os.HServiceManager
import java.io.FileDescriptor

abstract class BaseBinderHookHandle(private val serviceName: String) : BaseHookHandle(), IBinder {

    protected var proxyBinderObj: IBinder? = null
        private set

    init {
        proxyBinderObj = HServiceManager.getService.call(serviceName)
    }

    override fun hookInject(target: Any, proxy: Any) {
        replaceSystemService(serviceName)
    }

    override fun getInterfaceDescriptor(): String? {
        return proxyBinderObj?.interfaceDescriptor
    }

    override fun pingBinder(): Boolean {
        return proxyBinderObj?.pingBinder() ?: false
    }

    override fun isBinderAlive(): Boolean {
        return proxyBinderObj?.isBinderAlive() ?: false
    }

    override fun queryLocalInterface(descriptor: String): IInterface? {
        return proxyBinderObj?.queryLocalInterface(descriptor)
    }

    override fun dump(fd: FileDescriptor, args: Array<out String>?) {
        proxyBinderObj?.dump(fd, args)
    }

    override fun dumpAsync(fd: FileDescriptor, args: Array<out String>?) {
        proxyBinderObj?.dumpAsync(fd, args)
    }

    override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        return proxyBinderObj?.transact(code, data, reply, flags) ?: false
    }

    override fun linkToDeath(recipient: IBinder.DeathRecipient, flags: Int) {
       proxyBinderObj?.linkToDeath(recipient, flags)
    }

    override fun unlinkToDeath(recipient: IBinder.DeathRecipient, flags: Int): Boolean {
        return proxyBinderObj?.unlinkToDeath(recipient, flags) ?: false
    }


    protected open fun replaceSystemService(name: String) {
        val services: MutableMap<String, IBinder> = HServiceManager.sCache.get()
        services[name] = this
    }
}