package com.github.vilmosnagy.elq.elqcore.service

import com.avaje.ebean.Expr
import com.avaje.ebean.Expression
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import javax.inject.Inject

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
open class ExpressionBuilderService @Inject constructor() {

    open fun equals(propertyName: String, value: Any?): Expression {
        return Expr.eq(propertyName, value)
    }
}