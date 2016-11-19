package com.github.vilmosnagy.elq.elqcore.cache

import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class MethodReturnValueProvider<T> (
        internal val targetMethod: JVMMethod,
        internal val objectInvokedOn: Any?,
        internal val parameters: List<ValueProvider<T>> = listOf()
) : ValueProvider<T> {

    override fun getValue(cacheKey: T): Any? {
        val evaluatedParameters = parameters.map { p -> p.getValue(cacheKey) }
        return targetMethod.invoke(objectInvokedOn, *evaluatedParameters.toTypedArray())
    }

}