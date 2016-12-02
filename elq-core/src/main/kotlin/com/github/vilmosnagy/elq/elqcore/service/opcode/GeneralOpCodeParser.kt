package com.github.vilmosnagy.elq.elqcore.service.opcode

import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode
import com.github.vilmosnagy.elq.elqcore.model.statements.GetFieldStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareStatement
import com.github.vilmosnagy.elq.elqcore.pop
import org.apache.bcel.classfile.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import org.apache.bcel.classfile.Method as BCELMethod
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Singleton
internal class GeneralOpCodeParser @Inject constructor(
        private val invokeOpCodeParser: InvokeOpCodeParser
) {

    private fun getIndexedOpCodeList(opCodesList: List<OpCode>): List<Pair<Int, OpCode>> {
        val opCodeIndexed = opCodesList.mapIndexed { i, opCode -> i to opCode }
        return opCodesList.mapIndexed { idx, opCode ->
            val sum = opCodeIndexed.filter { p -> p.first < idx }.sumBy { p -> p.second.type.otherByteCount + 1 }
            sum to opCode
        }
    }

    fun parseExpressionList(opCodeList: List<OpCode>, bcelClass: JavaClass, jvmMethod: java.lang.reflect.Method, stack: Deque<Statement> = ArrayDeque()): Statement {
        val opCodes = getIndexedOpCodeList(opCodeList)
        var nextIndex = 0

        for ((index, opCode) in opCodes) {
            if (nextIndex <= index) {
                when (opCode) {
                    is OpCode.ConstToStackOperation -> addConstToStack(opCode, stack, bcelClass.constantPool)
                    is OpCode.ReturnOperation<*> -> evaluateReturn(opCode, stack, jvmMethod)
                    is OpCode.VariableToStackOperation -> evaluateVariableToStack(opCode, stack)
                    is OpCode.InvokeFunctionOperation -> evaluateInvokeFunctionOperation(opCode, stack, bcelClass)
                    is OpCode.JumpOperation -> nextIndex = index + opCode.branchIndex
                    is OpCode.BranchOperation -> {
                        evaluateBranchOperation(opCode, stack, index, opCodes, bcelClass, jvmMethod)
                        nextIndex = Integer.MAX_VALUE
                    }
                    is OpCode.getfield -> evaluateGetField(opCode, stack, bcelClass.constantPool, bcelClass.className)
                    is OpCode.dup -> stack.push(stack.peekFirst())
                }
            }
        }

        return stack.pop()
    }

    private fun evaluateGetField(opCode: OpCode.getfield, stack: Deque<Statement>, constantPool: ConstantPool, className: String) {
        /* TODO there's a reference on the top of the stack of the object whom the field is retrieved from.
         * it should be stored in the GetFieldStatement.
         */
        stack.pop()
        val fieldRef = constantPool.getConstant(opCode.indexReference) as ConstantFieldref
        val fieldName = (constantPool.getConstant(fieldRef.nameAndTypeIndex) as ConstantNameAndType).getName(constantPool)
        stack.push(GetFieldStatement(Class.forName(className), fieldName))
    }

    private fun evaluateBranchOperation(opCode: OpCode.BranchOperation, stack: Deque<Statement>, index: Int, opCodes: List<Pair<Int, OpCode>>, bcelClass: JavaClass, jvmMethod: java.lang.reflect.Method) {
        val subOpCodeListOnNotJumpingBranch = opCodes.filter { p -> p.first > index }.map { p -> p.second }.toList()
        val subOpCodeListOnJumpingBranch = opCodes.filter { p -> p.first >= (index + opCode.branchIndex) }.map { p -> p.second }.toList()
        val branchIfNotJumpedOperations = parseExpressionList(subOpCodeListOnNotJumpingBranch, bcelClass, jvmMethod, ArrayDeque(stack))
        val branchIfJumpedOperations = parseExpressionList(subOpCodeListOnJumpingBranch, bcelClass, jvmMethod, ArrayDeque(stack))

        val compareStatement: CompareStatement
        // TODO
        if (opCode is OpCode.BiCompareBranchOperation) {
            compareStatement = CompareStatement(
                    stack.pop() as Statement.EvaluableStatement<*>,
                    stack.pop() as Statement.EvaluableStatement<*>,
                    opCode.compareType)
        } else if (opCode is OpCode.UniCompareBranchOperation) {
            compareStatement = CompareStatement(
                    stack.pop() as Statement.EvaluableStatement<*>,
                    Statement.LoadConstant(opCode.staticValue),
                    opCode.compareType)
        } else {
            TODO()
        }
        stack.push(BranchedStatement(compareStatement, branchIfNotJumpedOperations, branchIfJumpedOperations))
    }

    private fun evaluateInvokeFunctionOperation(opCode: OpCode.InvokeFunctionOperation, stack: Deque<Statement>, bcelClass: JavaClass) {
        val methodRef = bcelClass.constantPool.getConstant(opCode.indexReference) as ConstantCP
        val methodNameAndType = bcelClass.constantPool.getConstant(methodRef.nameAndTypeIndex) as ConstantNameAndType
        val className = methodRef.getClass(bcelClass.constantPool)
        val methodName = methodNameAndType.getName(bcelClass.constantPool)
        val methodSignature = methodNameAndType.getSignature(bcelClass.constantPool)

        val methodCount = invokeOpCodeParser.getParameterCount(opCode, methodSignature)
        val methodParameters = stack.pop(methodCount)
        // TODO should do something with void methods...
        stack.push(invokeOpCodeParser.parseInvokeOpCode(opCode, className, methodName, methodSignature, methodParameters))
    }

    private fun evaluateVariableToStack(opCode: OpCode.VariableToStackOperation, stack: Deque<Statement>) {
        stack.push(
                when (opCode) {
                    is OpCode.VariableToStackOperation -> Statement.LoadVariable(opCode.variableIndex)
                    else -> throw IllegalStateException()
                })
    }

    private fun evaluateReturn(opCode: OpCode.ReturnOperation<*>, stack: Deque<Statement>, jvmMethod: java.lang.reflect.Method) {
        when (opCode) {
            is OpCode.IntReturnOperation -> {
                val lastElementInStack = getValueFromStackElement(stack.pop())
                if (lastElementInStack is Statement) {
                    stack.push(Statement.ReturnStatement(lastElementInStack))
                } else {
                    val value: Any = if (jvmMethod.returnType == Boolean::class.java) {
                        lastElementInStack == 1
                    } else {
                        lastElementInStack as Int
                    }
                    stack.push(Statement.ReturnStatement(Statement.LoadConstant(value)))
                }
            }
            is OpCode.ObjectReturnOperation -> {
                if (jvmMethod.returnType == Void::class.java || jvmMethod.returnType == Void.TYPE) {
                    stack.push(Statement.VoidReturnStatement())
                } else {
                    stack.push(Statement.ReturnStatement(stack.pop()))
                }
            }
            else -> TODO()
        }
    }

    private fun getValueFromStackElement(stackElement: Statement): Any? = when (stackElement) {
        is Statement.LoadConstant<*> -> stackElement.value
        is Statement.LoadVariable -> stackElement
        is Statement.LazyEvaluatedStatement<*> -> stackElement
        is GetFieldStatement -> stackElement
        else -> TODO()
    }

    private fun addConstToStack(opCode: OpCode.ConstToStackOperation, stack: Deque<Statement>, constantPool: ConstantPool) {
        stack.push(
                when (opCode) {
                    is OpCode.IntToStackOperation -> Statement.LoadConstant(opCode.value)
                    is OpCode.ldc -> Statement.LoadConstant(getConstantFromConstPool(opCode.indexByte, constantPool))
                    else -> throw IllegalStateException()
                }
        )
    }

    private fun getConstantFromConstPool(indexByte: Int, constantPool: ConstantPool): Any {
        val constant = constantPool.getConstant(indexByte)
        return when (constant) {
            is ConstantString -> constant.getBytes(constantPool)
            else -> TODO()
        }

    }

}
