package com.virtual.box.core.server

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import com.tencent.mmkv.MMKV
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.manager.VmServiceManager

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmServiceProvider: ContentProvider() {

    private val logger = Logger.getLogger(L.SERVER_TAG,"VmServiceProvider")

    override fun onCreate(): Boolean {
        logger.d("服务进程开始启动中。。。")
        MMKV.initialize(context!!)
        // 初始化服务
        VmFileSystem.initSystem(context!!)
        VmServiceManager.initService()
        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        logger.methodLine("【IPC】","method = %s, arg = %s, extras = %s", method, arg, extras)
        if (VM_METHOD_NAME == method && extras != null){
            val bundle = Bundle()
            val vmServiceName = extras.getString(VM_SERVICE_NAME_KEY)!!
            bundle.putBinder(VM_SERVICE_BINDER_KEY, VmServiceManager.getService(vmServiceName))
            return bundle
        }
        return super.call(method, arg, extras)
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null

    override fun getType(uri: Uri): String = ""

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    companion object {
        /**
         * 跨进程调用方法名称
         */
        const val VM_METHOD_NAME = "_VM_"

        /**
         * 服务端 bundle 名称
         */
        const val VM_SERVICE_NAME_KEY = "_VM_|_server_name_"

        /**
         * bundle key值，存的是服务端Binder对象
         */
        const val VM_SERVICE_BINDER_KEY = "_VM_|_server_"

        const val VM_AT_REQ_KEY = "_VM_|_at_req_key"

        const val VM_AT_RES_KEY = "_VM_|_at_res_key"
    }
}