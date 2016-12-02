@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service.opcode

import com.github.vilmosnagy.elq.elqcore.model.FunctionIntegerIsFive
import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode.*
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement.ReturnStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType.NOT_EQUALS
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever

import io.kotlintest.specs.FeatureSpec
import org.apache.bcel.Const
import org.apache.bcel.Repository
import org.apache.bcel.classfile.ConstantString
import org.apache.bcel.classfile.ConstantUtf8
import org.apache.bcel.classfile.JavaClass
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.lang.Integer
import org.apache.bcel.classfile.Method as BCELMethod
import java.lang.Boolean as JavaBoolean
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class GeneralOpCodeParserTest : FeatureSpec() {

    @Mock
    private lateinit var invokeOpCodeParser: InvokeOpCodeParser

    @InjectMocks
    private lateinit var testObj: GeneralOpCodeParser

    init {
        MockitoAnnotations.initMocks(this)
        feature("Parses simple return function's") {
            val bcelClass: JavaClass = spy(Repository.lookupClass(FakeTestClass::class.java))
            val returnBooleanJvmMethod: JVMMethod = FakeTestClass::class.java.getDeclaredMethod("returnBoolean")
            val returnIntJvmMethod: JVMMethod = FakeTestClass::class.java.getDeclaredMethod("returnInt")

            scenario("Parses simple return function's boolean return value (true)") {
                val opCodeList = listOf(
                        iconst_1(), ireturn()
                )
                val actualExpressionList = testObj.parseExpressionList(opCodeList, bcelClass, returnBooleanJvmMethod)
                val expectedExpressionList = ReturnStatement(Statement.LoadConstant(true))
                actualExpressionList shouldBe expectedExpressionList
            }

            scenario("Parses simple return function's boolean return value (false)") {
                val opCodeList = listOf(
                        iconst_0(), ireturn()
                )
                val actualExpressionList = testObj.parseExpressionList(opCodeList, bcelClass, returnBooleanJvmMethod)
                val expectedExpressionList = ReturnStatement(Statement.LoadConstant(false))
                actualExpressionList shouldBe expectedExpressionList
            }

            scenario("Parses simple return function's integer return value (0)") {
                val opCodeList = listOf(
                        iconst_0(), ireturn()
                )
                val actualExpressionList = testObj.parseExpressionList(opCodeList, bcelClass, returnIntJvmMethod)
                val expectedExpressionList = ReturnStatement(Statement.LoadConstant(0))
                actualExpressionList shouldBe expectedExpressionList
            }

            scenario("Parses simple return function's integer return value (5)") {
                val opCodeList = listOf(
                        iconst_5(), ireturn()
                )
                val actualExpressionList = testObj.parseExpressionList(opCodeList, bcelClass, returnIntJvmMethod)
                val expectedExpressionList = ReturnStatement(Statement.LoadConstant(5))
                actualExpressionList shouldBe expectedExpressionList
            }
        }

        feature("Parses simple 'some field' equals to a constant value functions's") {
            val bcelClass: JavaClass = spy(Repository.lookupClass(FunctionIntegerIsFive::class.java))
            val jvmMethod: JVMMethod = FunctionIntegerIsFive::class.java.getDeclaredMethod("invoke", Integer::class.java)
            scenario("Parses 'some field' ('id') equals to constant integer value (5)") {

                val intValueCallStatement = MethodCallStatement(Integer::class.java, Integer::class.java.getDeclaredMethod("intValue"), listOf(), Integer.TYPE)
                val booleanValueOfCallStatement0 = MethodCallStatement(JavaBoolean::class.java, JavaBoolean::class.java.getDeclaredMethod("valueOf", JavaBoolean.TYPE), listOf(), JavaBoolean::class.java)
                val booleanValueOfCallStatement1 = MethodCallStatement(JavaBoolean::class.java, JavaBoolean::class.java.getDeclaredMethod("valueOf", JavaBoolean.TYPE), listOf(), JavaBoolean::class.java)

                whenever(invokeOpCodeParser.getParameterCount(invokevirtual(0, 2), "()I")).thenReturn(1)
                whenever(invokeOpCodeParser.parseInvokeOpCode(
                        invokevirtual(0, 2), "java.lang.Integer", "intValue",
                        "()I", listOf(Statement.LoadVariable(1)))).thenReturn(intValueCallStatement)

                whenever(invokeOpCodeParser.getParameterCount(invokestatic(0, 3), "(Z)Ljava/lang/Boolean;")).thenReturn(1)
                whenever(invokeOpCodeParser.parseInvokeOpCode(
                        invokestatic(0, 3), "java.lang.Boolean", "valueOf",
                        "(Z)Ljava/lang/Boolean;", listOf(Statement.LoadConstant(0)))).thenReturn(booleanValueOfCallStatement0)
                whenever(invokeOpCodeParser.parseInvokeOpCode(
                        invokestatic(0, 3), "java.lang.Boolean", "valueOf",
                        "(Z)Ljava/lang/Boolean;", listOf(Statement.LoadConstant(1)))).thenReturn(booleanValueOfCallStatement1)

                val actualExpressionList = testObj.parseExpressionList(
                        listOf(
                                aload_1(), invokevirtual(0, 2), iconst_5(),
                                if_icmpne(0, 7), iconst_1(), goto(0, 4),
                                iconst_0(), invokestatic(0, 3), areturn()
                        ),
                        bcelClass,
                        jvmMethod
                )
                val expectedExpressionList = BranchedStatement(
                        CompareStatement(Statement.LoadConstant(5), intValueCallStatement, NOT_EQUALS),
                        ReturnStatement(booleanValueOfCallStatement1),
                        ReturnStatement(booleanValueOfCallStatement0)
                )
                actualExpressionList shouldBe expectedExpressionList
            }
        }

        feature("Parses load constant from constant pool operation") {
            val bcelClass: JavaClass = mock(Mockito.RETURNS_DEEP_STUBS)
            val jvmMethod: JVMMethod = FakeTestClass::class.java.getDeclaredMethod("returnInt")
            scenario("Loads String constant from constant pool") {
                val constantString = ConstantString(22)
                val constantUtf8 = ConstantUtf8("it")

                whenever(bcelClass.constantPool.getConstant(23)).thenReturn(constantString)
                whenever(bcelClass.constantPool.getConstant(22, Const.CONSTANT_Utf8)).thenReturn(constantUtf8)
                val actualExpressionList = testObj.parseExpressionList(
                        listOf(ldc(23), areturn()),
                        bcelClass,
                        jvmMethod
                )

                val expectedExpressionList = ReturnStatement(Statement.LoadConstant("it"))
                actualExpressionList shouldBe expectedExpressionList
            }
        }
    }
}


internal class FakeTestClass {
    fun returnBoolean(): Boolean = true
    fun returnInt(): Int = 0
}