package com.github.vilmosnagy.elq.elqcore.model.statements

import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
interface Statement {

    fun evaluate(): Any? {
        return this;
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

    interface LazyEvaluatedStatement<T>: EvaluableStatement<T>

    data class LoadConstant<out T>(override val value: T) : EvaluableStatement<T>

    data class LoadVariable(val variableIndex: Int) : Statement

    data class ReturnStatement<out T>(override val value: T) : EvaluableStatement<T>
    class VoidReturnStatement(): Statement

}