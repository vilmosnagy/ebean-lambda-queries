package com.github.vilmosnagy.elq.elqcore.cache

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
interface ValueProvider<T> {

    fun getValue(cacheKey: T): Any?

}

data class ConstantValueProvider<T>(private val value: Any?) : ValueProvider<T> {
    override fun getValue(cacheKey: T): Any? {
        return value
    }
}