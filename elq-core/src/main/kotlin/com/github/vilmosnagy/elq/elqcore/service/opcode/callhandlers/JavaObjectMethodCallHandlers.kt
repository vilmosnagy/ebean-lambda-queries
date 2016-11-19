@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCall

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal class JavaObjectEqualsMethodCallHandler : MethodCallHandler {

    override fun getMethodCall(): MethodCall? {
        return null;
    }

    override fun handles(methodCall: MethodCall): Boolean {
        return methodCall.targetMethod.name == "equals" &&
                methodCall.targetMethod.parameterTypes.size == 1 &&
                methodCall.targetMethod.parameterTypes[0] == Object::class.java
    }

    override fun handleSpecial(parameters: List<Statement>): Statement {
        return BranchedStatement(
                CompareStatement(parameters[0], parameters[1], CompareType.NOT_EQUALS),
                Statement.LoadConstant(true),
                Statement.LoadConstant(false)
        );
    }

}