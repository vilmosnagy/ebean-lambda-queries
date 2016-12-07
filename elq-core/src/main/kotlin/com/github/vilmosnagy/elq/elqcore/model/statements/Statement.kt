package com.github.vilmosnagy.elq.elqcore.model.statements

import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal interface Statement {

    fun evaluate(): Any? {
        return this;
    }

    fun deepEvaluate(): Any? {
        val evaluated = evaluate()
        return when(evaluated) {
            is BranchedStatement -> return evaluated.evaluateIfStraightForward().evaluate()
            else -> evaluated
        }
    }

    interface EvaluableStatement<out T> : Statement {
        val value: T

        override fun evaluate(): Any? {
            var currentValue: Any? = value
            while (currentValue is EvaluableStatement<*>) {
                currentValue = currentValue.value
            }
            return currentValue
        }
    }

    interface LazyEvaluatedStatement : Statement

    data class LoadConstant<out T>(override val value: T) : EvaluableStatement<T>

    data class LoadVariable(val variableIndex: Int) : Statement

    data class ReturnStatement<out T>(override val value: T) : EvaluableStatement<T>
    class VoidReturnStatement(): Statement

}