package com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers

import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCodeType
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import java.lang.reflect.Method

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
interface MethodCallHandler {

    enum class MethodCallType {
        INVOKE_STATIC, INVOKE_DYNAMIC, INVOKE_SPECIAL, INVOKE_VIRTUAL
    }

    data class MethodCall(
            val methodCallType: MethodCallType,
            val targetClass: Class<*>,
            val targetMethod: Method
    )

    fun handles(methodCall: MethodCall): Boolean {
        return methodCall == getMethodCall()
    }

    fun getMethodCall(): MethodCall?

    fun handle(methodCall: MethodCall, parameters: List<Statement>): MethodCallStatement<*> {
        val statement = handleSpecial(parameters)

        return MethodCallStatement<Any>(
                targetClass = methodCall.targetClass,
                targetMethod = methodCall.targetMethod,
                returnType = methodCall.targetMethod.returnType,
                evaluatedStatement = statement
        )
    }

    fun handleSpecial(parameters: List<Statement>): Statement

}