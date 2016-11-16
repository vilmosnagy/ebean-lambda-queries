package com.github.vilmosnagy.elq.elqcore.cache

import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
data class MethodReturnValueProvider<T> (
        private val targetMethod: JVMMethod,
        private val objectInvokedOn: Any?,
        private val parameters: List<ValueProvider<T>> = listOf()
) : ValueProvider<T> {

    override fun getValue(cacheKey: T): Any? {
        val evaluatedParameters = parameters.map { p -> p.getValue(cacheKey) }
        return targetMethod.invoke(objectInvokedOn, *evaluatedParameters.toTypedArray())
    }

}