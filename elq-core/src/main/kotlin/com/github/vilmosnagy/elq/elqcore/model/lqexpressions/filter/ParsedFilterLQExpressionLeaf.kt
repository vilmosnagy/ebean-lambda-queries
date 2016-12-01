package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter

import com.github.vilmosnagy.elq.elqcore.cache.ValueProvider
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType

internal data class ParsedFilterLQExpressionLeaf<ENTITY_TYPE> (
        internal val fieldName: String,
        internal val value: ValueProvider<Predicate<ENTITY_TYPE>>,
        internal val compareType: CompareType
) : ExpressionNode<ENTITY_TYPE> {

    override fun <EXPRESSION_TYPE> buildExpression(expressionBuilder: ExpressionBuilder<EXPRESSION_TYPE>, predicate: Predicate<ENTITY_TYPE>): EXPRESSION_TYPE {
        val evaluatedValue = value.getValue(predicate)
        return when(compareType) {
            CompareType.EQUALS ->                       expressionBuilder.equals(fieldName, evaluatedValue)
            CompareType.GREATER_THAN_OR_EQUALS ->       expressionBuilder.greaterThanOrEquals(fieldName, evaluatedValue)
            CompareType.LESS_THAN ->                    expressionBuilder.lessThan(fieldName, evaluatedValue)
            CompareType.LESS_THAN_OR_EQUALS ->          expressionBuilder.lessThanOrEquals(fieldName, evaluatedValue)
            CompareType.GREATER_THAN ->                 expressionBuilder.greaterThan(fieldName, evaluatedValue)
            CompareType.NOT_EQUALS ->                   expressionBuilder.notEquals(fieldName, evaluatedValue)
        }
    }
}