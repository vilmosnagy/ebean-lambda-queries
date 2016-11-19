@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service.opcode

import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCall
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCallType
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandlerService
import com.github.vilmosnagy.elq.elqcore.subStringBetween
import java.lang.reflect.Method
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal open class InvokeOpCodeParser @Inject constructor(
        private val methodCallHandlerService: MethodCallHandlerService
) {

    open fun parseInvokeOpCode(opCode: OpCode.InvokeFunctionOperation,
                               targetClassName: String, methodName: String, methodSignature: String,
                               parameters: List<Statement>): MethodCallStatement<*> {
        val methodParameterList = getMethodParameters(methodSignature)
        val targetClass = Class.forName(targetClassName)
        val targetMethod = targetClass.getDeclaredMethod(methodName, *methodParameterList.toTypedArray())
        val returnType = targetMethod.returnType

        val methodCall = getMethodCall(opCode, targetClass, targetMethod)
        return if (methodCallHandlerService.requiresSpecialHandle(methodCall)) {
            methodCallHandlerService.handleSpecialMethodCall(methodCall, parameters)
        } else {
            MethodCallStatement(targetClass, targetMethod, parameters, returnType, null)
        }
    }

    private fun getMethodCall(opCode: OpCode.InvokeFunctionOperation, targetClass: Class<*>, targetMethod: Method): MethodCall {
        val methodCallType = when (opCode) {
            is OpCode.invokestatic -> MethodCallType.INVOKE_STATIC
            is OpCode.invokevirtual -> MethodCallType.INVOKE_VIRTUAL
            is OpCode.invokespecial -> MethodCallType.INVOKE_SPECIAL
            else -> TODO()
        }
        return MethodCall(methodCallType, targetClass, targetMethod)
    }

    open fun getParameterCount(opCode: OpCode.InvokeFunctionOperation, methodSignature: String): Int {
        val methodParameterList = getMethodParameters(methodSignature)

        return if (opCode is OpCode.invokevirtual) {
            methodParameterList.size + 1
        } else {
            methodParameterList.size
        }
    }

    private fun getMethodParameters(methodSignature: String): List<Class<*>> {
        val parameterDefinition = methodSignature.subStringBetween('(', ')')
        val pattern = Pattern.compile("((\\[)*(Z|B|S|C|I|J|F|D|L(\\w+\\/)*\\w+;))")
        val matcher = pattern.matcher(parameterDefinition)
        val methodParameterList = mutableListOf<Class<*>>()
        while (matcher.find()) {
            val arrayIndicators = matcher.group(2)
            val typeDefinition = matcher.group(3)
            methodParameterList += parseClassDefinition(arrayIndicators, typeDefinition)
        }
        return methodParameterList
    }

    private fun parseClassDefinition(arrayIndicators: String?, typeDefinition: String): Class<*> {
        return if (arrayIndicators != null && arrayIndicators.isNotEmpty()) {
            Class.forName(arrayIndicators + typeDefinition.replace('/', '.'))
        } else if (typeDefinition.startsWith('L')) {
            val className = typeDefinition.substring(1, typeDefinition.length - 1).replace('/', '.')
            Class.forName(className)
        } else {
            when (typeDefinition) {
                "Z" -> java.lang.Boolean.TYPE
                "B" -> java.lang.Byte.TYPE
                "S" -> java.lang.Short.TYPE
                "C" -> java.lang.Character.TYPE
                "J" -> java.lang.Long.TYPE
                "I" -> java.lang.Integer.TYPE
                "F" -> java.lang.Float.TYPE
                "D" -> java.lang.Double.TYPE
                else -> throw IllegalStateException()
            }
        }
    }

}

