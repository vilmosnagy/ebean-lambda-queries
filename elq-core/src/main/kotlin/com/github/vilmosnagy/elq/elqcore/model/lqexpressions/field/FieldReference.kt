package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.field

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
data class FieldReference(
        val parentClazz: Class<*>,
        val fieldName: String,
        val nextReference: FieldReference?
) {
    val fullReference: String by lazy { calcFullReference(this, StringBuilder()) }

    companion object {
        private tailrec fun calcFullReference(currentReference: FieldReference, accumulator: StringBuilder): String {
            return if (currentReference.nextReference == null) {
                accumulator.append(currentReference.fieldName).toString()
            } else {
                accumulator.append(currentReference.fieldName).append('.')
                calcFullReference(currentReference.nextReference, accumulator)
            }
        }
    }
}