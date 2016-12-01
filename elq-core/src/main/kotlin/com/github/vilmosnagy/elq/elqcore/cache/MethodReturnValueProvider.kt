package com.github.vilmosnagy.elq.elqcore.cache

import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
internal data class MethodReturnValueProvider<PREDICATE_TYPE> (
        internal val targetMethod: JVMMethod,
        internal val parameters: List<ValueProvider<PREDICATE_TYPE>> = listOf()
) : ValueProvider<PREDICATE_TYPE> {

    override fun getValue(cacheKey: PREDICATE_TYPE): Any? {
        val evaluatedParameters = parameters.map { p -> p.getValue(cacheKey) }
        // TODO do something with non-static methods
        return targetMethod.invoke(null, *evaluatedParameters.toTypedArray())
    }

}