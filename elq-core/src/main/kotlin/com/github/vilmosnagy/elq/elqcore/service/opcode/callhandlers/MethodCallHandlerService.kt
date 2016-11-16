package com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers

import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCall
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Singleton
open class MethodCallHandlerService @Inject constructor() {

    private val specialMethodCallHandlers: MutableList<MethodCallHandler> = mutableListOf(
            JavaBooleanValueOfMethodCallHandler(), KotlinInternalEqualsMethodCallHandler(), JavaObjectEqualsMethodCallHandler(), KotlinInternalThrowUninitializedPropertyAccessExceptionMethodCallHandler()
    )

    open fun requiresSpecialHandle(methodCall: MethodCall): Boolean {
        return specialMethodCallHandlers.any { it.handles(methodCall) }
    }

    open fun handleSpecialMethodCall(methodCall: MethodCall, parameters: List<Statement>): MethodCallStatement<*> {
        val handlers = specialMethodCallHandlers.filter { it.handles(methodCall) }.toList()
        if (handlers.size != 1) {
            throw IllegalStateException()
        }
        return handlers[0].handle(methodCall, parameters)
    }
}