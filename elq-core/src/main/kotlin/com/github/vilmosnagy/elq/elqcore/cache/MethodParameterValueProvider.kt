package com.github.vilmosnagy.elq.elqcore.cache

import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.MethodCallChainProxies
import com.github.vilmosnagy.elq.elqcore.service.proxy.ArgumentFromPropertyCallChainInterceptor
import net.sf.cglib.proxy.Enhancer
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
data class MethodParameterValueProvider<T>(internal val variableIndex: Int, internal val propertyCallChain: List<JVMMethod>): ValueProvider<Predicate<T>> {

    override fun getValue(cacheKey: Predicate<T>): Any? {
        val (proxiedInstance, interceptorChain) = getProxiedInstance<T>(propertyCallChain)
        cacheKey.test(proxiedInstance)
        return interceptorChain.getLastCapturedArguments()?.get(variableIndex) ?: null
    }

    private fun <T> getProxiedInstance(propertyCallChain: List<JVMMethod>): MethodCallChainProxies<T> {
        val proxiedClass: Class<T> = propertyCallChain[0].declaringClass as Class<T>
        val interceptor = ArgumentFromPropertyCallChainInterceptor(propertyCallChain)
        return MethodCallChainProxies(Enhancer.create(proxiedClass, interceptor) as T, interceptor)
    }
}