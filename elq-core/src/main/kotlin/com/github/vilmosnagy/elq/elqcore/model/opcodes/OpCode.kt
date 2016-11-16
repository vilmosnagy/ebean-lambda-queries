package com.github.vilmosnagy.elq.elqcore.model.opcodes

import com.github.vilmosnagy.elq.elqcore.EqualsAndHashCode
import com.github.vilmosnagy.elq.elqcore.IncludedArgs
import com.github.vilmosnagy.elq.elqcore.includedArgs
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
sealed class OpCode(val type: OpCodeType): EqualsAndHashCode() {

    override val included = includedArgs(type)

    interface VariableToStackOperation {
        val variableIndex: Int
    }

    class iload_0(): OpCode(OpCodeType.iload_0), VariableToStackOperation {
        override val variableIndex = 0
    }

    class aload_0(): OpCode(OpCodeType.aload_0), VariableToStackOperation {
        override val variableIndex = 0
    }
    class aload_1(): OpCode(OpCodeType.aload_1), VariableToStackOperation {
        override val variableIndex = 1
    }

    interface TwoByteIndexOperation {
        val indexByte1: Int
        val indexByte2: Int
        val indexReference: Int
            get() = indexByte1 * 256 + indexByte2
    }

    interface InvokeFunctionOperation: TwoByteIndexOperation

    class invokestatic(override val indexByte1: Int, override  val indexByte2: Int): OpCode(OpCodeType.invokestatic), InvokeFunctionOperation {
        override val included = includedArgs(type, indexReference)
    }

    class invokevirtual(override val indexByte1: Int, override  val indexByte2: Int): OpCode(OpCodeType.invokevirtual), InvokeFunctionOperation {
        override val included = includedArgs(type, indexReference)
    }

    class invokespecial(override val indexByte1: Int, override  val indexByte2: Int): OpCode(OpCodeType.invokespecial), InvokeFunctionOperation {
        override val included = includedArgs(type, indexReference)
    }

    interface ConstToStackOperation

    class ldc(val indexByte: Int): OpCode(OpCodeType.ldc), ConstToStackOperation
    class dup: OpCode(OpCodeType.dup)

    interface IntToStackOperation: ConstToStackOperation {
        val value: Int
    }

    class iconst_0(): OpCode(OpCodeType.iconst_0), IntToStackOperation {
        override val value = 0
    }

    class iconst_1(): OpCode(OpCodeType.iconst_1), IntToStackOperation {
        override val value = 1
    }

    class iconst_2(): OpCode(OpCodeType.iconst_2), IntToStackOperation {
        override val value = 2
    }

    class iconst_3(): OpCode(OpCodeType.iconst_3), IntToStackOperation {
        override val value = 3
    }

    class iconst_4(): OpCode(OpCodeType.iconst_4), IntToStackOperation {
        override val value = 4
    }

    class iconst_5(): OpCode(OpCodeType.iconst_5), IntToStackOperation {
        override val value = 5
    }

    class bipush(override val value: Int) : OpCode(OpCodeType.bipush), IntToStackOperation

    class sipush(byte1: Int, byte2: Int): OpCode(OpCodeType.sipush), IntToStackOperation {
        override val value = byte1 * 256 + byte2
    }


    interface BranchOperation {
        val branchByte1: Int
        val branchByte2: Int
        val compareType: CompareType
        val branchIndex: Int
            get() = branchByte1 * 256 + branchByte2
    }

    class if_icmpne(override val branchByte1: Int, override val branchByte2: Int): OpCode(OpCodeType.if_icmpne), BranchOperation {
        override val compareType = CompareType.NOT_EQUALS
        override val included = includedArgs(type, branchIndex)
    }

    class ifnonnull(override val branchByte1: Int, override val branchByte2: Int): OpCode(OpCodeType.ifnonnull), BranchOperation {
        override val compareType = CompareType.NON_NULL
        override val included = includedArgs(type, branchIndex)
    }

    interface ReturnOperation<T>
    interface IntReturnOperation: ReturnOperation<Int>
    interface ObjectReturnOperation: ReturnOperation<Any>

    class ireturn(): OpCode(OpCodeType.ireturn), IntReturnOperation
    class areturn(): OpCode(OpCodeType.areturn), ObjectReturnOperation

    interface JumpOperation {
        val branchByte1: Int
        val branchByte2: Int
        val branchIndex: Int
            get() = branchByte1 * 256 + branchByte2
    }
    class goto(override val branchByte1: Int, override val branchByte2: Int): OpCode(OpCodeType.goto), JumpOperation

    class getfield(override val indexByte1: Int, override val indexByte2: Int): OpCode(OpCodeType.getfield), TwoByteIndexOperation

}