@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.model.statements.kotlin.ThrowUninitializedPropertyAccessException
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCall
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCallType
import kotlin.jvm.internal.Intrinsics

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class KotlinInternalEqualsMethodCallHandler : MethodCallHandler {

    companion object {
        val METHOD_CALL = MethodCall(
                MethodCallType.INVOKE_STATIC,
                Intrinsics::class.java,
                Intrinsics::class.java.getDeclaredMethod("areEqual", Object::class.java, Object::class.java)
        )
    }

    override fun getMethodCall(): MethodCall {
        return METHOD_CALL;
    }

    override fun handleSpecial(parameters: List<Statement>): Statement {
        return BranchedStatement(
                CompareStatement(parameters[0], parameters[1], CompareType.NOT_EQUALS),
                Statement.LoadConstant(true),
                Statement.LoadConstant(false)
        );
    }

}

class KotlinInternalThrowUninitializedPropertyAccessExceptionMethodCallHandler : MethodCallHandler {

    companion object {
        val METHOD_CALL = MethodCall(
                MethodCallType.INVOKE_STATIC,
                Intrinsics::class.java,
                Intrinsics::class.java.getDeclaredMethod("throwUninitializedPropertyAccessException", java.lang.String::class.java)
        )
    }

    override fun getMethodCall(): MethodCall {
        return METHOD_CALL;
    }

    override fun handleSpecial(parameters: List<Statement>): Statement {
        return ThrowUninitializedPropertyAccessException
    }

}