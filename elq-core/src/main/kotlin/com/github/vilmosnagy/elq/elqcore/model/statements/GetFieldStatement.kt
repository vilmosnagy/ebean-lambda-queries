package com.github.vilmosnagy.elq.elqcore.model.statements

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
class GetFieldStatement(
        val javaClass: Class<*>,
        val fieldName: String
): Statement