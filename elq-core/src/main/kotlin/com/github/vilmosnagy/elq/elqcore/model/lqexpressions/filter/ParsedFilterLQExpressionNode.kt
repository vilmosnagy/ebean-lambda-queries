package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter

import com.github.vilmosnagy.elq.elqcore.cache.ValueProvider
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.LogicalType

internal data class ParsedFilterLQExpressionNode<ENTITY_TYPE> (
        internal val leftChild: ExpressionNode<ENTITY_TYPE>,
        internal val rightChild: ExpressionNode<ENTITY_TYPE>,
        internal val logicalType: LogicalType
) : ExpressionNode<ENTITY_TYPE> {

    override fun <EXPRESSION_TYPE> buildExpression(expressionBuilder: ExpressionBuilder<EXPRESSION_TYPE>, predicate: Predicate<ENTITY_TYPE>): EXPRESSION_TYPE {
        val lhs = leftChild.buildExpression(expressionBuilder, predicate)
        val rhs = rightChild.buildExpression(expressionBuilder, predicate)
        return when(logicalType) {
            LogicalType.AND -> expressionBuilder.and(lhs, rhs)
            LogicalType.OR -> expressionBuilder.or(lhs, rhs)
        }
    }
}