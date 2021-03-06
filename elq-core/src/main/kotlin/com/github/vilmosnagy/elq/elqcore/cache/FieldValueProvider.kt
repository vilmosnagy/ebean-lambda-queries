package com.github.vilmosnagy.elq.elqcore.cache

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class FieldValueProvider<T:Any>(private val fieldName: String): ValueProvider<T> {

    override fun getValue(cacheKey: T): Any? {
        val predicateClass = cacheKey.javaClass
        val field = predicateClass.declaredFields.first { it.name == fieldName }
        field.isAccessible = true
        return field.get(cacheKey)
    }

}