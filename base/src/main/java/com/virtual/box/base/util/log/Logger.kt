package com.virtual.box.base.util.log

import android.util.Log
import com.virtual.box.base.helper.AppHelper
import com.virtual.box.base.helper.SystemHelper

class Logger(private val mainTag: String) {

    private var tag: String = ""

    constructor(mainTag: String, tag: String) : this(mainTag){
        this.tag = tag
    }

    fun d(format: String, vararg args: Any?){
        log(Log.DEBUG,  format,  *args)
    }

    fun dd(subTag: String, format: String, vararg args: Any?){
        log(Log.DEBUG, subTag + format, *args)
    }

    fun i(format: String, vararg args: Any?){
        log(Log.INFO,  format,  *args)
    }

    fun ii(subTag: String, format: String, vararg args: Any?){
        log(Log.INFO, subTag + format, *args)
    }

    fun e(format: String, vararg args: Any?){
        log(Log.ERROR, format, *args)
    }

    fun ee(subTag: String, format: String, vararg args: Any?){
        log(Log.ERROR, subTag + format, *args)
    }

    fun e(e: Throwable){
        if (AppHelper.isDebug){
            Log.println(Log.ERROR,"[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$mainTag",
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            e.printStackTrace()
            Log.println(Log.ERROR,"[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$mainTag",
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        }
    }

    fun methodLine(firstLine: String, paramFormat: String, vararg args: Any?){
        if (AppHelper.isRelease){
            return
        }
        val stackTrace = Thread.currentThread().stackTrace
        val classFileName = stackTrace[3].fileName
        val line = stackTrace[3].lineNumber
        val methodName = stackTrace[3].methodName
        val formatString = paramFormat.replace(",", "\n")
        val printTag = if (tag.isNotEmpty()) "[$tag]#" else ""
        val printString = printTag + String.format("$methodName >> $firstLine >> ($classFileName:$line)\n $formatString", *args)
        if (SystemHelper.isMainProcess()){
            Log.println(Log.DEBUG, mainTag, printString)
        }else{
            Log.println(Log.DEBUG, "[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$mainTag", printString)
        }
    }

    fun method(paramFormat: String, vararg args: Any?){
        if (AppHelper.isRelease){
            return
        }
        val stackTrace = Thread.currentThread().stackTrace
        val classFileName = stackTrace[3].fileName
        val line = stackTrace[3].lineNumber
        val methodName = stackTrace[3].methodName
        val formatString = paramFormat.replace(",", "\n")
        log(Log.DEBUG,  "$methodName >> ($classFileName:$line)\n$formatString", *args)
    }

    private fun log(level: Int, format: String, vararg args: Any?){
        if (AppHelper.isRelease){
            return
        }
        val printTag = if (tag.isNotEmpty()) "[$tag] >> " else ""
        val printString = printTag + String.format(format, *args)
        if (SystemHelper.isMainProcess()){
            Log.println(level, mainTag, printString)
        }else{
            Log.println(level, "[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$mainTag", printString)
        }
    }

    companion object{

        private lateinit var sHookLogger: Logger
        private lateinit var sServerLogger: Logger
        private lateinit var sVirtualLogger: Logger
        @JvmStatic
        fun hookLogger(): Logger {
            if (!Companion::sHookLogger.isInitialized){
                synchronized(this){
                    if (!Companion::sHookLogger.isInitialized){
                        sHookLogger = Logger(L.HOOK_TAG)
                    }
                }
            }
            return sHookLogger
        }
        @JvmStatic
        fun serverLogger(): Logger {
            if (!Companion::sServerLogger.isInitialized){
                synchronized(this){
                    if (!Companion::sServerLogger.isInitialized){
                        sServerLogger = Logger(L.SERVER_TAG)
                    }
                }
            }
            return sHookLogger
        }

        @JvmStatic
        fun virtualLogger(): Logger {
            if (!Companion::sVirtualLogger.isInitialized){
                synchronized(this){
                    if (!Companion::sVirtualLogger.isInitialized){
                        sVirtualLogger = Logger(L.VM_TAG)
                    }
                }
            }
            return sVirtualLogger
        }
        @JvmStatic
        fun getLogger(tag: String) : Logger {
            return Logger(tag)
        }
        @JvmStatic
        fun getLogger(mainTag: String, tag: String): Logger {
            return Logger(mainTag, tag)
        }
    }
}