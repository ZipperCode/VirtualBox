package com.virtual.hook.processor

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 *
 * @author zhangzhipeng
 * @date   2022/5/19
 **/
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class LogProcessor: AbstractProcessor() {

    // 打印日志工具类
    private lateinit var mMessage: Messager

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (processingEnv == null) return
        mMessage = processingEnv.messager
        mMessage.printMessage(Diagnostic.Kind.NOTE, ">>>>>> LogProcessor 初始化完成 >>>>>")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(

        )
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        TODO("Not yet implemented")
    }
}