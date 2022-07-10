package com.virtual.box.core.hook.delegate

import android.app.ActivityThread
import android.os.Handler
import android.os.Message
import com.virtual.box.base.util.log.L
import com.virtual.box.core.hook.IInjectHook
import com.virtual.box.core.manager.AppActivityManager
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.os.HHandler
import java.util.concurrent.atomic.AtomicBoolean

class VmHandlerCallback : Handler.Callback, IInjectHook {

    private var originHandler: Handler? = null
    private var originHandlerCallback: Handler.Callback? = null

    private val handleMessageLock = AtomicBoolean(false)

    override fun handleMessage(msg: Message): Boolean {
        if (!handleMessageLock.getAndSet(true)) {
            try {
                when (msg.what) {
                    // < 9.0
                    LAUNCH_ACTIVITY,
                    // >= 9.0
                    EXECUTE_TRANSACTION -> {
                        if (AppActivityManager.restoreOriginAdnHandleActivity(msg.obj)) {
                            originHandler?.sendMessageAtFrontOfQueue(Message.obtain(msg))
                            return true
                        }
                    }
                }
                return originHandlerCallback?.handleMessage(msg) ?: false
            }catch (e: Throwable){
              L.printStackTrace(e)
            } finally {
                handleMessageLock.set(false)
            }
        }
        return false
    }

    override fun initHook() {
        originHandler = HActivityThread.mH[ActivityThread.currentActivityThread()]
        originHandlerCallback = HHandler.mCallback.get(originHandler)
        HHandler.mCallback.set(originHandler, this)
    }

    override fun isHooked(): Boolean {
        return originHandler != null && HHandler.mCallback.get(originHandler) == this
    }





    companion object {
        const val LAUNCH_ACTIVITY = 100
        const val PAUSE_ACTIVITY = 101
        const val PAUSE_ACTIVITY_FINISHING = 102
        const val STOP_ACTIVITY_SHOW = 103
        const val STOP_ACTIVITY_HIDE = 104
        const val SHOW_WINDOW = 105
        const val HIDE_WINDOW = 106
        const val RESUME_ACTIVITY = 107
        const val SEND_RESULT = 108
        const val DESTROY_ACTIVITY = 109

        const val BIND_APPLICATION = 110

        const val EXIT_APPLICATION = 111

        const val RECEIVER = 113

        const val CREATE_SERVICE = 114

        const val SERVICE_ARGS = 115

        const val STOP_SERVICE = 116

        const val CONFIGURATION_CHANGED = 118
        const val CLEAN_UP_CONTEXT = 119

        const val GC_WHEN_IDLE = 120

        const val BIND_SERVICE = 121

        const val UNBIND_SERVICE = 122
        const val DUMP_SERVICE = 123
        const val LOW_MEMORY = 124
        const val PROFILER_CONTROL = 127
        const val CREATE_BACKUP_AGENT = 128
        const val DESTROY_BACKUP_AGENT = 129
        const val SUICIDE = 130

        const val REMOVE_PROVIDER = 131
        const val DISPATCH_PACKAGE_BROADCAST = 133

        const val SCHEDULE_CRASH = 134
        const val DUMP_HEAP = 135
        const val DUMP_ACTIVITY = 136
        const val SLEEPING = 137
        const val SET_CORE_SETTINGS = 138
        const val UPDATE_PACKAGE_COMPATIBILITY_INFO = 139

        const val DUMP_PROVIDER = 141
        const val UNSTABLE_PROVIDER_DIED = 142
        const val REQUEST_ASSIST_CONTEXT_EXTRAS = 143
        const val TRANSLUCENT_CONVERSION_COMPLETE = 144

        const val INSTALL_PROVIDER = 145
        const val ON_NEW_ACTIVITY_OPTIONS = 146

        const val ENTER_ANIMATION_COMPLETE = 149
        const val START_BINDER_TRACKING = 150
        const val STOP_BINDER_TRACKING_AND_DUMP = 151
        const val LOCAL_VOICE_INTERACTION_STARTED = 154
        const val ATTACH_AGENT = 155
        const val APPLICATION_INFO_CHANGED = 156
        const val RUN_ISOLATED_ENTRY_POINT = 158
        const val EXECUTE_TRANSACTION = 159
        const val RELAUNCH_ACTIVITY = 160
        const val PURGE_RESOURCES = 161
        const val ATTACH_STARTUP_AGENTS = 162
        const val UPDATE_UI_TRANSLATION_STATE = 163
        const val SET_CONTENT_CAPTURE_OPTIONS_CALLBACK = 164
        const val DUMP_GFXINFO = 165

        const val INSTRUMENT_WITHOUT_RESTART = 170
        const val FINISH_INSTRUMENTATION_WITHOUT_RESTART = 171
    }
}