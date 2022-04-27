package com.virtual.box.core.helper

import android.os.Parcel
import androidx.annotation.WorkerThread
import com.virtual.box.base.ext.checkAndCreate
import com.virtual.box.base.util.log.L
import com.virtual.box.core.manager.VmFileEnvironment
import com.virtual.box.core.server.pm.entity.VmPackageInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object PackageHelper {

    private const val TAG = "LibraryHelper"

    @JvmStatic
    @WorkerThread
    @Throws(Exception::class)
    fun copyLibrary(apkFile: File, targetLibRootDir: File) {
        val startTime = System.currentTimeMillis()
        if (!targetLibRootDir.exists()) {
            targetLibRootDir.mkdirs()
        }
        ZipFile(apkFile).use {
            findAndCopyLibrary(it, targetLibRootDir)
        }
        L.sd("$TAG >> 文件：${apkFile.absolutePath} 运行库拷贝成功，耗时：${System.currentTimeMillis() - startTime}")
    }

    @Synchronized
    fun saveInstallPackageInfo(vmPackageInfo: VmPackageInfo, file: File) {
        file.checkAndCreate()
        FileOutputStream(file).use { output ->
            val parcal = Parcel.obtain()
            try {
                parcal.setDataPosition(0)
                vmPackageInfo.writeToParcel(parcal,0)
                val byte = parcal.marshall()
                output.write(byte)
            } finally {
                parcal.recycle()
            }
        }
    }

    @Synchronized
    fun loadInstallPackageInfo(packageName: String): VmPackageInfo{
        val packageInfoFile = VmFileEnvironment.getInstallAppPackageInfoFile(packageName)
        FileInputStream(packageInfoFile).use { input ->
            val parcal = Parcel.obtain()
            try {
                parcal.setDataPosition(0)
                val readBytes = input.readBytes()
                parcal.unmarshall(readBytes, 0, readBytes.size)
                parcal.setDataPosition(0)
                val vmPackageInfo = VmPackageInfo()
                vmPackageInfo.writeToParcel(parcal, 0)
                return vmPackageInfo
            } finally {
                parcal.recycle()
            }
        }
    }

    @Throws(Exception::class)
    private fun findAndCopyLibrary(zipFile: ZipFile, targetLibRootDir: File) {
        L.sd("LibraryHelper >> 查找并拷贝so库")
        val prefix = "lib/"
        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            var entryName = entry.name
            if (!entryName.startsWith(prefix)) {
                continue
            }
            entryName = entryName.replace("lib/", "")
            if (entryName.contains("armeabi-v7a")) {
                entryName = entryName.replace("armeabi-v7a", "arm")
            }
            if (entryName.contains("arm64-v8a")) {
                entryName = entryName.replace("arm64-v8a", "arm64")
            }
            val soFile = File(targetLibRootDir, entryName)
            if (!soFile.exists()) {
                if (soFile.parentFile?.exists() == false) {
                    soFile.parentFile?.mkdirs()
                }
                soFile.createNewFile()
            } else {
                if (soFile.length() == entry.size) {
                    continue
                }
            }
            val buffer = ByteArray(1024 * 512)
            L.sd("LibraryHelper >> 开始拷贝 $entryName 到 ${soFile.absolutePath}")
            FileOutputStream(soFile).use { output ->
                zipFile.getInputStream(entry).use { input ->
                    var count = input.read(buffer)
                    while (count > 0) {
                        output.write(buffer, 0, count)
                        count = input.read(buffer)
                    }
                }
            }
        }

    }

}