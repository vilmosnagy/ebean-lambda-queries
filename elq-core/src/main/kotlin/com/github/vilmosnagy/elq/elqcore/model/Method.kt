package com.github.vilmosnagy.elq.elqcore.model

import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode
import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode.VariableToStackOperation
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement.EvaluableStatement
import org.apache.bcel.classfile.ConstantCP
import org.apache.bcel.classfile.ConstantNameAndType
import org.apache.bcel.classfile.JavaClass
import java.util.*
import org.apache.bcel.classfile.Method as BCELMethod
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
data class Method (
        val jvmMethod: JVMMethod,
        val returnStatement: Statement
): EvaluableStatement<Statement> {
    override val value: Statement
        get() = returnStatement
}