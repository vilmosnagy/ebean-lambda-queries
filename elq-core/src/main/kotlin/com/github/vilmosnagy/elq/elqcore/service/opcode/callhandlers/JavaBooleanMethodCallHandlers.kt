@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCall
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCallType
import java.lang.Boolean

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class JavaBooleanValueOfMethodCallHandler: MethodCallHandler {

    companion object {
        val METHOD_CALL = MethodCall(MethodCallType.INVOKE_STATIC, Boolean::class.java, Boolean::class.java.getDeclaredMethod("valueOf", Boolean.TYPE))
    }

    override fun getMethodCall(): MethodCall {
        return METHOD_CALL;
    }

    override fun handleSpecial(parameters: List<Statement>): Statement {
        val value = parameters[0].evaluate()
        return evaluateAnyValue(value)
    }

    private fun evaluateAnyValue(value: Any?): Statement {
        return when (value) {
            is BranchedStatement -> evaluateBranchedStatement(value)
            else -> evaluateConstantToBoolean(value)
        }
    }

    private fun evaluateBranchedStatement(value: BranchedStatement): Statement {
        return BranchedStatement(
                value.compareStatement,
                evaluateAnyValue(value.branch01.evaluate()),
                evaluateAnyValue(value.branch02.evaluate())
        )
    }

    private fun evaluateConstantToBoolean(value: Any?) = if (value == 1 || value == true) {
        Statement.LoadConstant(true)
    } else {
        Statement.LoadConstant(false)
    }


}