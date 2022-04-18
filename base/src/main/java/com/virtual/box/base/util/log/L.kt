package com.virtual.box.base.util.log

import android.util.Log
import com.virtual.box.base.helper.AppHelper
import com.virtual.box.base.helper.SystemHelper

object L {

    const val HOST_TAG = "VirtualHost"

    const val VM_TAG = "VirtualCore"

    const val HOOK_TAG = "VirtualHook"

    const val SERVER_TAG = "BServer"

    @JvmStatic
    fun vd(format: String, vararg args: Any?){
        d(VM_TAG, format, *args)
    }

    @JvmStatic
    fun vdParam(paramFormat: String, vararg args: Any?){
        logParam(Log.DEBUG, VM_TAG, paramFormat, *args)
    }

    @JvmStatic
    fun vdParamTag(tag: String, paramFormat: String, vararg args: Any?){
        logParamTag(Log.DEBUG, VM_TAG, tag, paramFormat, *args)
    }

    @JvmStatic
    fun vdParam(tag: String, firstLine: String, vararg args:Any?){
        if (AppHelper.isRelease){
            return
        }
        val builder = StringBuilder();
        val vTag = if (!SystemHelper.isMainProcess()){
            "[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$tag"
        }else{
            tag
        }
        builder.append(firstLine)
        if (args.isNotEmpty()){
            builder.append(" >> \n")
        }
        args.forEachIndexed { index, any ->
            builder.append("Param[$index][${if (any == null) "" else any.javaClass.simpleName}] = $any").append("\n")
        }

        Log.println(Log.DEBUG, vTag, builder.toString())
    }

    @JvmStatic
    fun ve(format: String, vararg args: Any?){
        e(VM_TAG, format, *args)
    }

    @JvmStatic
    fun hd(format: String, vararg args: Any?){
        d(HOOK_TAG, format, *args)
    }

    @JvmStatic
    fun hdParam(paramFormat: String, vararg args: Any?){
        logParam(Log.DEBUG, HOOK_TAG, paramFormat, *args)
    }

    @JvmStatic
    fun hdParamTag(tag: String, paramFormat: String, vararg args: Any?){
        logParamTag(Log.DEBUG, HOOK_TAG, tag, paramFormat, *args)
    }

    @JvmStatic
    fun hdAndParamTag(tag: String, firstLine: String, paramFormat: String, vararg args: Any?){
        logParamTag(Log.DEBUG, HOOK_TAG, tag,firstLine, paramFormat, *args)
    }

    @JvmStatic
    fun he(format: String, vararg  args: Any?){
        e(HOOK_TAG, format, *args)
    }
    @JvmStatic
    fun sd(format: String, vararg args: Any?){
        d(SERVER_TAG,format, *args)
    }

    @JvmStatic
    fun sdParam(paramFormat: String, vararg args: Any?){
        logParam(Log.DEBUG, SERVER_TAG, paramFormat, *args)
    }

    @JvmStatic
    fun sdParamTag(tag: String, paramFormat: String, vararg args: Any?){
        logParamTag(Log.DEBUG, SERVER_TAG, tag, paramFormat, *args)
    }

    @JvmStatic
    fun se(e: Throwable?){
        e(SERVER_TAG, e)
    }

    @JvmStatic
    fun se(format: String, vararg args: Any?){
        e(SERVER_TAG,format, *args)
    }

    @JvmStatic
    fun d(tag: String, format: String, vararg args: Any?){
        log(Log.DEBUG, tag, format, *args)
    }
    @JvmStatic
    fun e(tag: String, format: String, vararg args: Any?){
        log(Log.ERROR, tag, format, *args)
    }

    private fun logParamTag(level: Int, mainTag: String, tag: String, paramFormat: String, vararg args: Any?){
        if (AppHelper.isRelease){
            return
        }
        val stackTrace = Thread.currentThread().stackTrace
        val classFileName = stackTrace[4].fileName
        val line = stackTrace[4].lineNumber
        val methodName = stackTrace[4].methodName
        log(level, mainTag, "[$tag] >> $methodName >> ($classFileName:$line) \n ${paramFormat.replace(",", "\n")}", *args)
    }

    private fun logParamTag(level: Int, mainTag: String, tag: String, firstLine: String, paramFormat: String, vararg args: Any?){
        if (AppHelper.isRelease){
            return
        }
        val stackTrace = Thread.currentThread().stackTrace
        val classFileName = stackTrace[4].fileName
        val line = stackTrace[4].lineNumber
        val methodName = stackTrace[4].methodName
        log(level, mainTag, "[$tag] >> $methodName >> $firstLine >> ($classFileName:$line) \n ${paramFormat.replace(",", "\n")}", *args)
    }

    private fun logParam(level: Int, mainTag: String, paramFormat: String, vararg args: Any?){
        if (AppHelper.isRelease){
            return
        }
        val stackTrace = Thread.currentThread().stackTrace
        val classFileName = stackTrace[4].fileName
        val methodName = stackTrace[4].methodName
        val line = stackTrace[4].lineNumber
        log(level, mainTag, "[($classFileName:$line)] >> $methodName >> \n ${paramFormat.replace(",", "\n")}", *args)
    }

    private fun log(level: Int, tag: String, format: String, vararg args: Any?){
        if (AppHelper.isDebug){
            if (SystemHelper.isMainProcess()){
                Log.println(level, tag, String.format(format, *args))
            }else{
                Log.println(level, "[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$tag", String.format(format, *args))
            }
        }
    }

    @JvmStatic
    fun printStackTrace(e: Throwable){
        if (AppHelper.isDebug){
            Log.println(Log.ERROR, "[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程",
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            e.printStackTrace()
            Log.println(Log.ERROR, "[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程",
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        }
    }

    @JvmStatic
    fun e(tag: String, e: Throwable?){
        if (AppHelper.isDebug){
            Log.println(Log.ERROR,"[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$tag",
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            e?.printStackTrace()
            Log.println(Log.ERROR,"[${SystemHelper.getCurrentProcessNameExcludePackage()}]进程:$tag",
                ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        }
    }
}