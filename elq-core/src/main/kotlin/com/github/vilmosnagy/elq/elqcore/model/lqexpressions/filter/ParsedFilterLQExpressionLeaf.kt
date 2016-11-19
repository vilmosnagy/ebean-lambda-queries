package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter

import com.github.vilmosnagy.elq.elqcore.cache.ValueProvider
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType

data class ParsedFilterLQExpressionLeaf<ENTITY_TYPE> (
        val fieldName: String,
        val value: ValueProvider<Predicate<ENTITY_TYPE>>,
        val compareType: CompareType
) {
    fun <EXPRESSION_TYPE> buildExpression(expressionBuilder: ExpressionBuilder<EXPRESSION_TYPE>, predicate: Predicate<ENTITY_TYPE>): EXPRESSION_TYPE {
        val evaluatedValue = value.getValue(predicate)
        return when(compareType) {
            CompareType.EQUALS ->                       expressionBuilder.equals(fieldName, evaluatedValue)
            CompareType.GREATER_THAN_OR_EQUALS ->       expressionBuilder.greaterThanOrEquals(fieldName, evaluatedValue)
            CompareType.LESS_THAN ->                    expressionBuilder.lessThan(fieldName, evaluatedValue)
            CompareType.LESS_THAN_OR_EQUALS ->          expressionBuilder.lessThanOrEquals(fieldName, evaluatedValue)
            CompareType.GREATER_THAN ->                 expressionBuilder.greaterThan(fieldName, evaluatedValue)
            CompareType.NOT_EQUALS ->                   TODO()
            CompareType.NON_NULL ->                     TODO()
        }
    }
}