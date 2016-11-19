package com.github.vilmosnagy.elq.elqcore.model.statements

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal class GetFieldStatement(
        internal val javaClass: Class<*>,
        internal val fieldName: String
): Statement