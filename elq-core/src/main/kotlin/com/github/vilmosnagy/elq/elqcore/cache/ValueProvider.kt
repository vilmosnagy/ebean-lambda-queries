package com.github.vilmosnagy.elq.elqcore.cache

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal interface ValueProvider<T> {

    fun getValue(cacheKey: T): Any?

}

internal data class ConstantValueProvider<T>(private val value: Any?) : ValueProvider<T> {
    override fun getValue(cacheKey: T): Any? {
        return value
    }
}