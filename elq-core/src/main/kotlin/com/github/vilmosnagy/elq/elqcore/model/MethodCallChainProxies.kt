package com.github.vilmosnagy.elq.elqcore.model

import com.github.vilmosnagy.elq.elqcore.service.proxy.ArgumentFromPropertyCallChainInterceptor

data class MethodCallChainProxies<T>(
        val proxiedInstance: T,
        val interceptors: ArgumentFromPropertyCallChainInterceptor
)