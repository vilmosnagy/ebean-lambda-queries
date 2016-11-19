package com.github.vilmosnagy.elq.elqcore.service

import com.avaje.ebean.Expr
import com.avaje.ebean.Expression
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal open class EbeanExpressionBuilder : ExpressionBuilder<Expression> {

    override fun greaterThan(fieldName: String, value: Any?) = Expr.gt(fieldName, value)
    override fun lessThanOrEquals(fieldName: String, value: Any?) = Expr.le(fieldName, value)
    override fun greaterThanOrEquals(fieldName: String, value: Any?) = Expr.ge(fieldName, value)
    override fun lessThan(fieldName: String, value: Any?) = Expr.lt(fieldName, value)
    override fun equals(fieldName: String, value: Any?) = Expr.eq(fieldName, value)

}