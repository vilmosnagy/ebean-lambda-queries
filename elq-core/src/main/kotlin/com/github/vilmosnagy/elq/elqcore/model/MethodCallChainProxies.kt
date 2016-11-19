package com.github.vilmosnagy.elq.elqcore.model

import com.github.vilmosnagy.elq.elqcore.service.proxy.ArgumentFromPropertyCallChainInterceptor

internal data class MethodCallChainProxies<T>(
        internal val proxiedInstance: T,
        internal val interceptors: ArgumentFromPropertyCallChainInterceptor
)