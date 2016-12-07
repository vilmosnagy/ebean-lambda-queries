package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.field

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
data class FieldReference(
        val parentClazz: Class<*>,
        val fieldName: String,
        val nextReference: FieldReference?
) {
    val fullReference: String by lazy { calcFullReference() }

    private fun calcFullReference(): String {
        return if (nextReference != null) {
            "$fieldName.${nextReference.fullReference}"
        } else {
            return fieldName
        }
    }
}