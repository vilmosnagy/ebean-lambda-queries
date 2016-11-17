package com.github.vilmosnagy.elq.elqcore.service

import com.github.vilmosnagy.elq.elqcore.model.Method
import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode
import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCodeType
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.service.opcode.GeneralOpCodeParser
import org.apache.bcel.Repository
import org.apache.bcel.classfile.Code
import org.apache.bcel.classfile.JavaClass
import javax.inject.Inject
import javax.inject.Singleton

import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Singleton
open class MethodParser @Inject constructor(
        val generalOpCodeParser: GeneralOpCodeParser
) {

    open fun parseMethod(classToParse: Class<*>, declaredMethod: JVMMethod): Method {
        val bcelClass = Repository.lookupClass(classToParse.name)
        val bcelMethod = bcelClass.getMethod(declaredMethod)
        val opCodes = codeToOpCodeList(bcelClass, bcelMethod.code)
        val returnStatement = generalOpCodeParser.parseExpressionList(opCodes, bcelClass, declaredMethod)
        return Method(declaredMethod, returnStatement)
    }

    private fun codeToOpCodeList(bcelClass: JavaClass, method: Code): List<OpCode> {
        val retList = mutableListOf<OpCode>()
        var i = 0
        val methodCode = method.code.map { if (it < 0) (256 + it.toInt()) else it.toInt() }
        while(i < methodCode.size) {
            val opcodeType = OpCodeType.values().firstOrNull { it.opCode == methodCode[i] }

            @Suppress("FoldInitializerAndIfToElvis")
            if (opcodeType == null) {
                TODO("No matching opcode: ${methodCode[i]}")
            }

            val element = opcodeType.createNew(methodCode.subList(i, methodCode.size), bcelClass)
            retList.add(element)
            i += opcodeType.otherByteCount + 1;
        }
        return retList
    }

    // TODO test
    open fun unravelMethodCallChain(evaluableStatement: Statement.EvaluableStatement<*>): Statement.EvaluableStatement<*> {
        var subMethodCallBody = evaluableStatement
        while (subMethodCallBody.value is MethodCallStatement<*>) {
            subMethodCallBody = (subMethodCallBody.value as MethodCallStatement<*>).value.returnStatement as Statement.EvaluableStatement<*>
        }
        return subMethodCallBody
    }

}