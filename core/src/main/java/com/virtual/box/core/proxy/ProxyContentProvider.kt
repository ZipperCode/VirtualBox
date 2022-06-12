package com.virtual.box.core.proxy

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Process
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.core.entity.VmAppConfig
import com.virtual.box.core.manager.VmAppActivityThread
import com.virtual.box.core.server.VmApplicationService

/**
 * ContentProvider 客户端进程基类
 * 要打开一个进程时，调用contentProvider进行初始化进程，并将[VmApplicationService] 对象通过Bundle返回
 */
open class ProxyContentProvider : ContentProvider() {
    companion object {
        /**
         * 初始化虚拟进程方法名称
         */
        const val IPC_VM_INIT_METHOD_NAME = "_VM_|_init_process_"

        /**
         * bundle包装IBinder服务的key值
         */
        const val IPC_VM_BINDER_HANDLE_KEY = "_VM_|_app_handle_"

        const val IPC_VM_CALLING_PID_KEY = "_VM_|_calling_pid_"
        const val IPC_VM_CALLING_UID_KEY = "_VM_|_calling_uid_"

        /**
         * 启动进程的pid
         */
        const val IPC_VM_CUR_PID_KEY = "_VM_|_current_pid_"

        /**
         * 启动进程的uid
         */
        const val IPC_VM_CUR_UID_KEY = "_VM_|_current_uid_"

        const val IPC_VM_PROXY_PROCESS_NAME_KEY = "_VM_|_current_process_name_"
    }
    override fun onCreate(): Boolean = false

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (method == IPC_VM_INIT_METHOD_NAME && extras != null) {
            extras.classLoader = VmAppConfig::class.java.classLoader
            val appConfig: VmAppConfig = extras.getParcelable(VmAppConfig.IPC_BUNDLE_KEY)!!
            VmAppActivityThread.initProcessAppConfig(appConfig)
            val bundle = Bundle()
            // 进程启动后返回VmActivityThread引用
            bundle.putBinder(IPC_VM_BINDER_HANDLE_KEY, VmAppActivityThread)
            // 返回调用者pid和uid，确定调用来源
            val callingPid = Binder.getCallingPid()
            val callingUid = Binder.getCallingUid()
            bundle.putInt(IPC_VM_CALLING_PID_KEY, callingPid)
            bundle.putInt(IPC_VM_CALLING_UID_KEY, callingUid)
            val pid = Process.myPid()
            val uid = Process.myUid()
            Process.myUserHandle()
            bundle.putInt(IPC_VM_CUR_PID_KEY, pid)
            bundle.putInt(IPC_VM_CUR_UID_KEY, uid)
            bundle.putString(IPC_VM_PROXY_PROCESS_NAME_KEY,SystemHelper.getCurrentProcessName())
            return bundle
        }
        return super.call(method, arg, extras)
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    class P0 : ProxyContentProvider()
    class P1 : ProxyContentProvider()
    class P2 : ProxyContentProvider()
    class P3 : ProxyContentProvider()
    class P4 : ProxyContentProvider()
    class P5 : ProxyContentProvider()
    class P6 : ProxyContentProvider()
    class P7 : ProxyContentProvider()
    class P8 : ProxyContentProvider()
    class P9 : ProxyContentProvider()
    class P10 : ProxyContentProvider()
    class P11 : ProxyContentProvider()
    class P12 : ProxyContentProvider()
    class P13 : ProxyContentProvider()
    class P14 : ProxyContentProvider()
    class P15 : ProxyContentProvider()
    class P16 : ProxyContentProvider()
    class P17 : ProxyContentProvider()
    class P18 : ProxyContentProvider()
    class P19 : ProxyContentProvider()
    class P20 : ProxyContentProvider()
    class P21 : ProxyContentProvider()
    class P22 : ProxyContentProvider()
    class P23 : ProxyContentProvider()
    class P24 : ProxyContentProvider()
    class P25 : ProxyContentProvider()
    class P26 : ProxyContentProvider()
    class P27 : ProxyContentProvider()
    class P28 : ProxyContentProvider()
    class P29 : ProxyContentProvider()
    class P30 : ProxyContentProvider()
    class P31 : ProxyContentProvider()
    class P32 : ProxyContentProvider()
    class P33 : ProxyContentProvider()
    class P34 : ProxyContentProvider()
    class P35 : ProxyContentProvider()
    class P36 : ProxyContentProvider()
    class P37 : ProxyContentProvider()
    class P38 : ProxyContentProvider()
    class P39 : ProxyContentProvider()
    class P40 : ProxyContentProvider()
    class P41 : ProxyContentProvider()
    class P42 : ProxyContentProvider()
    class P43 : ProxyContentProvider()
    class P44 : ProxyContentProvider()
    class P45 : ProxyContentProvider()
    class P46 : ProxyContentProvider()
    class P47 : ProxyContentProvider()
    class P48 : ProxyContentProvider()
    class P49 : ProxyContentProvider()
    class P50 : ProxyContentProvider()
    class P51 : ProxyContentProvider()
    class P52 : ProxyContentProvider()
    class P53 : ProxyContentProvider()
    class P54 : ProxyContentProvider()
    class P55 : ProxyContentProvider()
    class P56 : ProxyContentProvider()
    class P57 : ProxyContentProvider()
    class P58 : ProxyContentProvider()
    class P59 : ProxyContentProvider()
    class P60 : ProxyContentProvider()
    class P61 : ProxyContentProvider()
    class P62 : ProxyContentProvider()
    class P63 : ProxyContentProvider()
    class P64 : ProxyContentProvider()
    class P65 : ProxyContentProvider()
    class P66 : ProxyContentProvider()
    class P67 : ProxyContentProvider()
    class P68 : ProxyContentProvider()
    class P69 : ProxyContentProvider()
    class P70 : ProxyContentProvider()
    class P71 : ProxyContentProvider()
    class P72 : ProxyContentProvider()
    class P73 : ProxyContentProvider()
    class P74 : ProxyContentProvider()
    class P75 : ProxyContentProvider()
    class P76 : ProxyContentProvider()
    class P77 : ProxyContentProvider()
    class P78 : ProxyContentProvider()
    class P79 : ProxyContentProvider()
    class P80 : ProxyContentProvider()
    class P81 : ProxyContentProvider()
    class P82 : ProxyContentProvider()
    class P83 : ProxyContentProvider()
    class P84 : ProxyContentProvider()
    class P85 : ProxyContentProvider()
    class P86 : ProxyContentProvider()
    class P87 : ProxyContentProvider()
    class P88 : ProxyContentProvider()
    class P89 : ProxyContentProvider()
    class P90 : ProxyContentProvider()
    class P91 : ProxyContentProvider()
    class P92 : ProxyContentProvider()
    class P93 : ProxyContentProvider()
    class P94 : ProxyContentProvider()
    class P95 : ProxyContentProvider()
    class P96 : ProxyContentProvider()
    class P97 : ProxyContentProvider()
    class P98 : ProxyContentProvider()
    class P99 : ProxyContentProvider()

}