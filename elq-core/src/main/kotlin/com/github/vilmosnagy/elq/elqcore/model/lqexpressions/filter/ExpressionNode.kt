package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter

import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate

/**
 * TODO docs
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
public interface ExpressionNode<ENTITY_TYPE> {

    fun <EXPRESSION_TYPE> buildExpression(expressionBuilder: ExpressionBuilder<EXPRESSION_TYPE>, predicate: Predicate<ENTITY_TYPE>): EXPRESSION_TYPE

}