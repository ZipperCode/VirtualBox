package com.virtual.hook.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.virtual.hook.annotator.HookMethod
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class HookProcessor: AbstractProcessor() {

    // 打印日志工具类
    private lateinit var mMessage: Messager

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (processingEnv == null) return
        mMessage = processingEnv.messager
        mMessage.printMessage(Diagnostic.Kind.NOTE, "HookProcessor 初始化完成")
    }


    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            HookMethod::class.java.canonicalName
        )
    }

    override fun process(typeElementSet: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        println("typeElementSet = $typeElementSet")
        println("roundEnvironment = $roundEnvironment")
        val elementMap = LinkedHashMap<Element, ArrayList<Element>>()
        roundEnvironment?.getElementsAnnotatedWith(HookMethod::class.java)?.forEach { element ->
            //注解 属性 名称
            println("element.simpleName ------------> ${element.simpleName}")
            //注解 所在类 名称
            println("element.enclosingElement.simpleName ------------> ${element.enclosingElement.simpleName}")
            println("element.kind.name --------> ${element.kind.name}")

            var methodElementList = elementMap[element.enclosingElement]
            if (methodElementList == null){
                methodElementList = ArrayList()
                elementMap[element.enclosingElement] = methodElementList
            }
            methodElementList.add(element)
        }

        elementMap.forEach { element, methodElementList ->
            println("clazz------------> ${element.simpleName}")
            // 包名
            val targetPks = processingEnv.elementUtils.getPackageOf(element)
            val targetCls = ClassName(targetPks.toString(), element.simpleName.toString())
            val helperProxyMapProp = PropertySpec.builder("proxyMethodMap", Map::class.java)
                .addModifiers(KModifier.PRIVATE)
                .mutable(false)
                .initializer("""mutableMapOf<String, IHook>()""")
                .build()

            val helperConstructFunc = FunSpec.constructorBuilder()

            val helperCls = TypeSpec.classBuilder("${targetCls}ProxyMethodHelper")
                .addProperty(helperProxyMapProp)

            // 方法
            methodElementList.forEach { methodElement ->
                val methodNameKey = methodElement.getAnnotation(HookMethod::class.java)
                // 获取包名

                // 获取宿主类名

                // 获取当前类名

                helperConstructFunc.addStatement("this.%M[%S] = %T","proxyMethodMap",methodNameKey,methodElement.simpleName)
            }
            val file = FileSpec.builder(targetPks.toString(),"${targetCls}ProxyMethodHelper")
                .addType(helperCls.addFunction(helperConstructFunc.build()).build())


        }

        return true
    }
}