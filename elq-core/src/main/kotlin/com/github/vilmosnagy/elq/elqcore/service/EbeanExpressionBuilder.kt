package com.github.vilmosnagy.elq.elqcore.service

import com.avaje.ebean.Expr
import com.avaje.ebean.Expression
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
internal class EbeanExpressionBuilder : ExpressionBuilder<Expression> {
    override fun and(lhs: Expression, rhs: Expression): Expression = Expr.and(lhs, rhs)
    override fun or(lhs: Expression, rhs: Expression): Expression = Expr.or(lhs, rhs)
    override fun greaterThan(fieldName: String, value: Any?): Expression = Expr.gt(fieldName, value)
    override fun lessThanOrEquals(fieldName: String, value: Any?): Expression = Expr.le(fieldName, value)
    override fun greaterThanOrEquals(fieldName: String, value: Any?): Expression = Expr.ge(fieldName, value)
    override fun lessThan(fieldName: String, value: Any?): Expression = Expr.lt(fieldName, value)
    override fun equals(fieldName: String, value: Any?): Expression = Expr.eq(fieldName, value)
    override fun notEquals(fieldName: String, value: Any?): Expression = Expr.ne(fieldName, value)
}