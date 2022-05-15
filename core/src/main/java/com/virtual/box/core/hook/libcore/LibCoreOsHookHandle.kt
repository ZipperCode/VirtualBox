package com.virtual.box.core.hook.libcore

import android.system.StructStat
import android.system.StructStatVfs
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.hook.BaseHookHandle
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.reflect.libcore.io.HLibcore
import java.io.FileDescriptor


/**
 *
 * @author zhangzhipeng
 * @date   2022/4/25
 **/
@Suppress("UNUSED")
class LibCoreOsHookHandle: BaseHookHandle() {

    private val logger = Logger.getLogger(L.HOOK_TAG)

    override fun getOriginObject(): Any? {
        return HLibcore.os.get()
    }

    override fun hookInject(target: Any, proxy: Any) {
        HLibcore.os.set(proxy)
    }

    override fun isHooked(): Boolean {
        return HLibcore.os.get() !== proxyInvocation
    }

    fun access(methodHandle: MethodHandle, path: String?, mode: Int): Boolean {
        logger.e(">> Libcore.os#access path = %s", path)
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath, mode)) as Boolean
    }

    fun chmod(methodHandle: MethodHandle, path: String?, mode: Int) {
        logger.e(">> Libcore.os#chmod path = %s", path)
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, mode))
    }

    fun chown(methodHandle: MethodHandle, path: String?, uid: Int, gid: Int)  {
        logger.e(">> Libcore.os#chown path = %s", path)
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, uid, gid))
    }

    fun lchown(methodHandle: MethodHandle, path: String?, uid: Int, gid: Int) {
        logger.e(">> Libcore.os#lchown path = %s", path)
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, uid, gid))
    }

    fun link(methodHandle: MethodHandle, oldPath: String?, newPath: String?) {
        logger.e(">> Libcore.os#link oldPath = %s, newPath = %s", oldPath, newPath)
        val oldRedirectPath = VmCore.redirectPath(oldPath)
        val newRedirectPath = VmCore.redirectPath(newPath)
        methodHandle.invokeOriginMethod(arrayOf(oldRedirectPath, newRedirectPath))
    }

    fun lstat(methodHandle: MethodHandle, path: String?): StructStat? {
        logger.e(">> Libcore.os#lstat path = %s", path)
        val oldRedirectPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(oldRedirectPath)) as? StructStat
    }

    fun mkdir(methodHandle: MethodHandle, path: String?, mode: Int) {
        logger.e(">> Libcore.os#mkdir path = %s", path)
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, mode))
    }

    fun mkfifo(methodHandle: MethodHandle, path: String?, mode: Int) {
        logger.e(">> Libcore.os#mkfifo path = %s", path)
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, mode))
    }

    fun open(methodHandle: MethodHandle, path: String?, flags: Int, mode: Int): FileDescriptor? {
        logger.e(">> Libcore.os#open path = %s", path)
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath, flags, mode)) as? FileDescriptor
    }

    fun readlink(methodHandle: MethodHandle, path: String?): String? {
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath)) as? String
    }

    fun realpath(methodHandle: MethodHandle, path: String?): String? {
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath)) as? String
    }

    fun remove(methodHandle: MethodHandle,path: String?) {
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath))
    }

    fun rename(methodHandle: MethodHandle,oldPath: String?, newPath: String?) {
        val oldRedirectPath = VmCore.redirectPath(oldPath)
        val newRedirectPath = VmCore.redirectPath(newPath)
        methodHandle.invokeOriginMethod(arrayOf(oldRedirectPath, newRedirectPath))
    }

    fun stat(methodHandle: MethodHandle, path: String?): StructStat? {
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath)) as? StructStat
    }

    fun statvfs(methodHandle: MethodHandle, path: String?): StructStatVfs? {
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath)) as? StructStatVfs
    }

    fun symlink(methodHandle: MethodHandle,oldPath: String?, newPath: String?) {
        val oldRedirectPath = VmCore.redirectPath(oldPath)
        val newRedirectPath = VmCore.redirectPath(newPath)
        methodHandle.invokeOriginMethod(arrayOf(oldRedirectPath, newRedirectPath))
    }

    fun execv(methodHandle: MethodHandle,filename: String?, argv: Array<String?>?) {
        logger.e("Libcore.os#execv filename = %s", filename)
        val oldFilename = VmCore.redirectPath(filename)
        logger.e("Libcore.os#execv redirect filename = %s", oldFilename)
        methodHandle.invokeOriginMethod(arrayOf(oldFilename, argv))
    }

    fun execve(methodHandle: MethodHandle, filename: String?, argv: Array<String?>?, envp: Array<String?>?) {
        logger.e("Libcore.os#execve filename = %s", filename)
        val oldFilename = VmCore.redirectPath(filename)
        logger.e("Libcore.os#execve redirect filename = %s", oldFilename)
        methodHandle.invokeOriginMethod(arrayOf(oldFilename, argv, envp))
    }

    fun getxattr(methodHandle: MethodHandle,path: String?, name: String?): ByteArray? {
        val newPath = VmCore.redirectPath(path)
        return methodHandle.invokeOriginMethod(arrayOf(newPath, name)) as? ByteArray
    }

    fun removexattr(methodHandle: MethodHandle,path: String?, name: String?) {
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, name))
    }

    fun setxattr(methodHandle: MethodHandle,path: String?, name: String?, value: ByteArray?, flags: Int) {
        val newPath = VmCore.redirectPath(path)
        methodHandle.invokeOriginMethod(arrayOf(newPath, name, value, flags))
    }

    fun unlink(methodHandle: MethodHandle,pathname: String?) {
        val newPath = VmCore.redirectPath(pathname)
        methodHandle.invokeOriginMethod(arrayOf(newPath))
    }
}