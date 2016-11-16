package com.github.vilmosnagy.elq.elqcore.service.proxy

import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class ArgumentFromPropertyCallChainInterceptor(val propertyCallChain: List<Method>) : MethodInterceptor {

    val original: Any by lazy { propertyCallChain[0].declaringClass.newInstance() }
    var nextInterceptor: ArgumentFromPropertyCallChainInterceptor? = null
    var capturedArguments: Array<out Any?>? = null

    override fun intercept(proxy: Any?, method: Method?, args: Array<out Any?>?, mproxy: MethodProxy?): Any? {
        return if (propertyCallChain.size > 1) {
            if (method == propertyCallChain[0]) {
                val proxiedClass: Class<*> = propertyCallChain[1].declaringClass
                nextInterceptor = ArgumentFromPropertyCallChainInterceptor(propertyCallChain.drop(1))
                return Enhancer.create(proxiedClass, nextInterceptor)
            } else {
                invokeOriginal(args, method)
            }
        } else {
            if (method == propertyCallChain[0]) {
                capturedArguments = args
                return getDefaultValue(method.returnType)
            } else {
                invokeOriginal(args, method)
            }
        }
    }

    private fun invokeOriginal(args: Array<out Any?>?, method: Method?): Any? {
        val varargs = args ?: arrayOf()
        return method?.invoke(original, *varargs)
    }

    private fun <T> getDefaultValue(clazz: Class<T>): T {
        return java.lang.reflect.Array.get(java.lang.reflect.Array.newInstance(clazz, 1), 0) as T
    }

    fun getLastCapturedArguments(): Array<out Any?>? {
        val nextInterceptor = this.nextInterceptor
        return if (nextInterceptor != null) {
            nextInterceptor.getLastCapturedArguments()
        } else {
            capturedArguments
        }
    }

}