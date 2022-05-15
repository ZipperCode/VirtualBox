package com.virtual.box.base.util

import android.os.*
import android.util.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author zhangzhipeng
 * @date   2022/3/31
 **/
class AppExecutors private constructor() {
    companion object{
        private lateinit var sInstance: AppExecutors

        private val CPU_CORE = Runtime.getRuntime().availableProcessors()

        @JvmStatic
        fun get(): AppExecutors{
            if (!::sInstance.isInitialized){
                synchronized(this){
                    if (!::sInstance.isInitialized){
                        sInstance = AppExecutors()
                    }
                }
            }
            return sInstance
        }
    }

    private val backgroundExecutor: ThreadPoolExecutor

    private val defaultExecutor: ThreadPoolExecutor

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    private val lock = ConditionVariable()

    init {
        defaultExecutor = object :ThreadPoolExecutor(CPU_CORE, 128,
            10, TimeUnit.SECONDS, SynchronousQueue(), DefaultThreadFactory()){
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT)
            }
        }
        backgroundExecutor = object :ThreadPoolExecutor(2, Int.MAX_VALUE,
            30, TimeUnit.SECONDS, SynchronousQueue(), CustomThreadFactory()){
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            }
        }
        defaultExecutor.rejectedExecutionHandler = RejectedExecutionHandler { r, _ -> backgroundExecutor.execute(r) }
        backgroundExecutor.allowCoreThreadTimeOut(true)
    }

    fun executeMultiThreadWithLock(task: Runnable){
        synchronized(this){
            execute{
                task.run()
                lock.open()
            }
            lock.block()
        }
    }

    inline fun<reified T> executeMultiThreadWithLockAsResult(task: Callable<T>): T?{
        val lock = ConditionVariable()
        var result: T? = null
        synchronized(this){
            execute{
                result = task.call()
                lock.open()
            }
            lock.block()
            return result
        }
    }

    fun execute(runnable: Runnable){
        defaultExecutor.execute(runnable)
    }

    fun doBackground(runnable: Runnable){
        backgroundExecutor.execute(runnable)
    }

    fun executeMain(runnable: Runnable){
        mainHandler.post(runnable)
    }

    fun runOnMainThread(runnable: Runnable){
        if (Looper.myLooper() == Looper.getMainLooper()){
            runnable.run()
        }else{
            executeMain(runnable)
        }
    }

    fun getDefaultExecutor() : ExecutorService{
        return defaultExecutor
    }

    fun getBackgroundExecutor(): ExecutorService {
        return backgroundExecutor
    }

    fun getMainHandler() : Handler{
        return mainHandler
    }

    private class DefaultThreadFactory: ThreadFactory{
        private val mCount = AtomicInteger(1)

        override fun newThread(r: Runnable?): Thread {
            return Thread(r,"BackgroundThread #${mCount.getAndIncrement()}").apply {
                priority = Thread.NORM_PRIORITY
            }
        }
    }

    private class CustomThreadFactory: ThreadFactory{
        private val mCount = AtomicInteger(1)

        override fun newThread(r: Runnable?): Thread {
            return Thread(r,"BackgroundThread #${mCount.getAndIncrement()}").apply {
                priority = Thread.MIN_PRIORITY
            }
        }
    }

    private class MonitorExecutor() {
        private val handlerThread: HandlerThread = HandlerThread("DefaultSubThread")
        private val handler: Handler = Handler(handlerThread.looper)

        companion object{
            private val DURATION = arrayOf(32L, 100L, 500L, 1000L, 3000L)
            private const val autoPrintDuration = 50L
        }

        private val runMap: HashMap<Long, LogRunnable> = HashMap<Long, LogRunnable>()


        init {
            for (duration in DURATION) {
                runMap[duration] = LogRunnable(duration)
            }
            Looper.getMainLooper().setMessageLogging { log ->
                if (log.startsWith(">>>>> Dispatching")) {
                    startMonitor()
                }else if (log.startsWith("<<<<< Finished")) {
                    endMonitor()
                }
            }
        }

        private fun startMonitor() {
            for (duration in runMap.keys) {
                handler.postDelayed(runMap[duration]!!, duration)
            }
        }

        private fun endMonitor() {
            for (duration in runMap.keys) {
                handler.removeCallbacks(runMap[duration]!!)
            }
        }

        class LogRunnable(var duration: Long) : Runnable {
            override fun run() {
                if (duration < autoPrintDuration) {
                    return
                }
                val stackTraceElements = Looper.getMainLooper().thread.stackTrace
                val builder = StringBuilder()
                for (element in stackTraceElements) {
                    builder.append(element.toString())
                    builder.append("\n")
                }
                val log = builder.toString()
                Log.e("MainThreadMonitor","【主线程耗时】" + duration + "ms\n" + log)
            }
        }
    }
}