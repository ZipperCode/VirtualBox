package com.virtual.box.base.storage

import android.os.Parcel
import android.os.Parcelable

object ParcelDataHelper {

    const val PackageResolverInfo = "PackageResolverInfo"

    private val dataStorageMap: MutableMap<String, IDataStorage> = HashMap()

    inline fun<reified T> saveMap(storageName: String, key: String, data: Map<*, T>) {
        val iDataStorage: IDataStorage = getDataStorageLock(storageName)
        val parcel = Parcel.obtain()
        try {
            parcel.setDataPosition(0)
            parcel.readMap(data, T::class.java.classLoader)
            val bytes = parcel.marshall()
            iDataStorage.save(key, bytes)
        } finally {
            parcel.recycle()
        }
    }

    inline fun<reified T> loadMap(storageName: String, key: String, clazz: Class<T>): Map<String, T>{
        val iDataStorage: IDataStorage = getDataStorageLock(storageName)
        val map = HashMap<String, T>()
        val parcel = Parcel.obtain()
        try {
            val bytes = iDataStorage.load(key, ByteArray(0))
            if (bytes.isEmpty()){
                return map
            }
            parcel.setDataPosition(0)
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)
            parcel.writeMap(map)
        } finally {
            parcel.recycle()
        }
        return map
    }

    inline fun <reified T: Parcelable> saveList(dataSource: IDataStorage, key: String, dataList: List<T>){
        val parcel = Parcel.obtain()
        try {
            parcel.setDataPosition(0)
            parcel.writeArray(dataList.toTypedArray())
            val bytes = parcel.marshall()
            dataSource.save(key, bytes)
        } finally {
            parcel.recycle()
        }
    }

    inline fun <reified T: Parcelable> loadList(dataSource: IDataStorage, key: String, clazz: Class<T>): List<T>{
        val parcel = Parcel.obtain()
        val result = ArrayList<T>()
        try {
            val bytes = dataSource.load(key, ByteArray(0))
            if (bytes.isEmpty()){
                return emptyList()
            }
            parcel.setDataPosition(0)
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)
            val readArray = parcel.readArray(T::class.java.classLoader) ?: return result
            for (any in readArray) {
                result.add(any as T)
            }
        } finally {
            parcel.recycle()
        }
        return result
    }

    fun getDataStorageLock(storageName: String): IDataStorage{
        synchronized(dataStorageMap){
            return if (!dataStorageMap.containsKey(storageName)){
                MapDataStorageImpl(storageName).apply {
                    dataStorageMap[storageName] = this
                }
            }else{
                dataStorageMap[storageName]!!
            }
        }
    }
}