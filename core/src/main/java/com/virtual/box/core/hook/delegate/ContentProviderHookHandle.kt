package com.virtual.box.core.hook.delegate

import android.content.*
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.*
import com.virtual.box.core.hook.BaseHookHandle
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.reflect.android.HAttributionSourceState
import com.virtual.box.reflect.android.content.HAttributionSource
import java.io.FileNotFoundException

class ContentProviderHookHandle : BaseHookHandle() {

    fun wrapper(contentProviderProxy: IInterface, appPkg: String): IInterface? {
        target = contentProviderProxy
        hostPkg = appPkg
        initHook()
        return proxyInvocation as IInterface?
    }
    override fun getOriginObject(): Any? {
        return target
    }

    override fun hookInject(target: Any, proxy: Any) {}

    override fun isHooked(): Boolean {
        return true
    }

    fun query(
        methodHandle: MethodHandle,
        attributionSource: Any?, url: Uri?,
        projection: Array<String?>?,
        queryArgs: Bundle?, cancellationSignal: Any?
    ): Cursor? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Cursor?
    }

    @Deprecated("maxTargetSdk = Build.VERSION_CODES.Q")
    @Throws(RemoteException::class)
    fun insert(methodHandle: MethodHandle, callingPkg: String?, url: Uri?, initialValues: ContentValues?): Uri? {
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg, url, initialValues)) as Uri?
    }

    @Throws(RemoteException::class)
    fun insert(
        methodHandle: MethodHandle,
        attributionSource: Any?, url: Uri?,
        initialValues: ContentValues?, extras: Bundle?
    ): Uri? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Uri?
    }

    @Deprecated("maxTargetSdk = Build.VERSION_CODES.Q")
    @Throws(RemoteException::class)
    fun bulkInsert(methodHandle: MethodHandle, callingPkg: String?, url: Uri?, initialValues: Array<ContentValues?>?): Int {
        return methodHandle.invokeOriginMethod(
            arrayOf(
                hostPkg, url, initialValues
            )
        ) as Int
    }

    @Throws(RemoteException::class)
    fun bulkInsert(
        methodHandle: MethodHandle,
        attributionSource: Any?, url: Uri?,
        initialValues: Array<ContentValues?>?
    ): Int {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Int
    }

    @Deprecated("maxTargetSdk = Build.VERSION_CODES.Q")
    @Throws(RemoteException::class)
    fun delete(methodHandle: MethodHandle, callingPkg: String?, url: Uri?, selection: String?, selectionArgs: Array<String?>?): Int {
        return methodHandle.invokeOriginMethod(arrayOf(hostPkg, url, selection, selectionArgs)) as Int
    }

    @Throws(RemoteException::class)
    fun delete(methodHandle: MethodHandle, attributionSource: Any?, url: Uri?, extras: Bundle?): Int {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Int
    }

    @Deprecated("maxTargetSdk = Build.VERSION_CODES.Q")
    @Throws(RemoteException::class)
    fun update(
        methodHandle: MethodHandle,
        callingPkg: String?, url: Uri?, values: ContentValues?, selection: String?,
        selectionArgs: Array<String?>?
    ): Int {

        return methodHandle.invokeOriginMethod(
            arrayOf(
                hostPkg, url, values, selection, selectionArgs
            )
        ) as Int
    }

    @Throws(RemoteException::class)
    fun update(
        methodHandle: MethodHandle,
        attributionSource: Any?, url: Uri?, values: ContentValues?,
        extras: Bundle?
    ): Int {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Int
    }

    @Throws(RemoteException::class, FileNotFoundException::class)
    fun openFile(
        methodHandle: MethodHandle,
        attributionSource: Any?,
        url: Uri?, mode: String?, signal: Any?
    ): ParcelFileDescriptor? {

        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as ParcelFileDescriptor?

    }

    @Throws(RemoteException::class, FileNotFoundException::class)
    fun openAssetFile(
        methodHandle: MethodHandle,
        attributionSource: Any?,
        url: Uri?, mode: String?, signal: Any?
    ): AssetFileDescriptor? {

        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as AssetFileDescriptor?
    }

    @Throws(RemoteException::class, OperationApplicationException::class)
    fun applyBatch(
        methodHandle: MethodHandle,
        attributionSource: Any?,
        authority: String?, operations: ArrayList<ContentProviderOperation?>?
    ): Array<ContentProviderResult?>? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Array<ContentProviderResult?>?
    }

    @Deprecated("maxTargetSdk = Build.VERSION_CODES.Q")
    @Throws(RemoteException::class)
    fun call(
        methodHandle: MethodHandle,
        callingPkg: String?, method: String?,
        arg: String?, extras: Bundle?
    ): Bundle? {
        return methodHandle.invokeOriginMethod(
            arrayOf(
                hostPkg, method, arg, extras
            )
        ) as Bundle?
    }

    @Throws(RemoteException::class)
    fun call(
        methodHandle: MethodHandle,
        attributionSource: Any?, authority: String?,
        method: String?, arg: String?, extras: Bundle?
    ): Bundle? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Bundle?

    }

    @Throws(RemoteException::class)
    fun checkUriPermission(
        methodHandle: MethodHandle,
        attributionSource: Any?, uri: Uri?,
        uid: Int, modeFlags: Int
    ): Int {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Int
    }


    @Throws(RemoteException::class)
    fun canonicalize(methodHandle: MethodHandle, attributionSource: Any?, uri: Uri?): Uri? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Uri?
    }

    /**
     * A oneway version of canonicalize. The functionality is exactly the same, except that the
     * call returns immediately, and the resulting type is returned when available via
     * a binder callback.
     */
    @Throws(RemoteException::class)
    fun canonicalizeAsync(
        methodHandle: MethodHandle,
        attributionSource: Any?, uri: Uri?,
        callback: RemoteCallback?
    ) {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        methodHandle.invokeOriginMethod()
    }

    @Throws(RemoteException::class)
    fun uncanonicalize(methodHandle: MethodHandle, attributionSource: Any?, uri: Uri?): Uri? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Uri?

    }

    /**
     * A oneway version of uncanonicalize. The functionality is exactly the same, except that the
     * call returns immediately, and the resulting type is returned when available via
     * a binder callback.
     */
    @Throws(RemoteException::class)
    fun uncanonicalizeAsync(
        methodHandle: MethodHandle,
        attributionSource: Any?, uri: Uri?,
        callback: RemoteCallback?
    ) {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        methodHandle.invokeOriginMethod()
    }

    @Throws(RemoteException::class)
    fun refresh(
        methodHandle: MethodHandle,
        attributionSource: Any?, url: Uri?,
        extras: Bundle?, cancellationSignal: Any?
    ): Boolean {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as Boolean
    }

    @Throws(RemoteException::class, FileNotFoundException::class)
    fun openTypedAssetFile(
        methodHandle: MethodHandle,
        attributionSource: Any?,
        url: Uri?, mimeType: String?, opts: Bundle?, signal: Any?
    ): AssetFileDescriptor? {
        Helper.fixAttributionSource(attributionSource, hostPkg)
        return methodHandle.invokeOriginMethod() as AssetFileDescriptor?
    }

    object Helper {
        internal fun fixAttributionSource(attributionSource: Any?, hostPks: String) {
            attributionSource ?: return
            val attributionSourceState = HAttributionSource.mAttributionSourceState.get(attributionSource)
            attributionSourceState ?: return
            HAttributionSourceState.packageName.set(attributionSourceState, hostPks)
            val nextAttributionSource = HAttributionSource.getNext.call(attributionSource)
            nextAttributionSource ?: return
            fixAttributionSource(nextAttributionSource, hostPks)

        }
    }
}