package com.virtual.box.base.storage

import android.os.Parcel
import android.os.Parcelable
import androidx.core.util.AtomicFile
import com.virtual.box.base.ext.checkOrCreateDirAndFile
import com.virtual.box.base.ext.closeHandle
import com.virtual.box.base.util.log.L
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Constructor

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class FileParcelDataHandle<T: Parcelable>(private val filePath: String, private val clazz: Class<T>): IParcelDataHandle<T> {

    private val fd: File = File(filePath)

    private val lock: Any = Any()

    private val parcelConstructor: Constructor<T>

    init {
        fd.checkOrCreateDirAndFile()
        parcelConstructor = clazz.getConstructor(Parcel::class.java)
    }

    override fun save(key: String, parcelable: T?) {
        val parcel = Parcel.obtain()
        try {
            val atomicFile = AtomicFile(fd)
            var fos: FileOutputStream? = null
            parcel.setDataPosition(0)
            parcelable?.writeToParcel(parcel, 0)
            val byte = parcel.marshall()
            try {
                fos = atomicFile.startWrite()
                fos.write(byte)
                atomicFile.finishWrite(fos)
            } catch (e: IOException) {
                L.printStackTrace(e)
                atomicFile.failWrite(fos)
            } finally {
                fos.closeHandle()
            }
        } finally {
            parcel.recycle()
        }
    }

    override fun load(key: String): T? {
        synchronized(lock){
            val parcel = Parcel.obtain()
            try {
                val fis = FileInputStream(fd)
                val readBytes = fis.readBytes()
                parcel.unmarshall(readBytes, 0, readBytes.size)
                parcel.setDataPosition(0)
                return parcelConstructor.newInstance(parcel)
            }finally {
                parcel.recycle()
            }
        }
    }
}