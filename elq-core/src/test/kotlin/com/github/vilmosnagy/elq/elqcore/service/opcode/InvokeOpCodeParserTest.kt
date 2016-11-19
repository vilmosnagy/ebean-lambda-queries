@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service.opcode

import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCall
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandler.MethodCallType.INVOKE_STATIC
import com.github.vilmosnagy.elq.elqcore.service.opcode.callhandlers.MethodCallHandlerService
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.lang.Boolean
import java.lang.Byte
import java.lang.Double
import java.lang.Float
import java.lang.Long
import java.lang.Short
import java.util.*

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class InvokeOpCodeParserTest : FeatureSpec() {

    @Mock
    private lateinit var methodCallHandlerService: MethodCallHandlerService

    @InjectMocks
    private lateinit var testObj: InvokeOpCodeParser

    init {
        MockitoAnnotations.initMocks(this)
        val mockedInvokeVirtual = OpCode.invokevirtual(0, 0)
        val mockedInvokeStatic = OpCode.invokestatic(0, 0)

        feature("Should return correct parameter count based on method signature") {
            scenario("()I") {
                testObj.getParameterCount(mockedInvokeVirtual, "()I") shouldBe 1
            }
            scenario("(Z)Ljava/lang/Boolean;") {
                testObj.getParameterCount(mockedInvokeStatic, "(Z)Ljava/lang/Boolean;") shouldBe 1
            }
        }

        feature("Should return correct method call statement based on method signarute for all primitive type") {
            scenario("Integer.valueOf(int): Integer") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(I)Ljava/lang/Integer;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Integer.TYPE)
                methodCallStatement.returnType shouldBe Integer::class.java
            }

            scenario("Short.valueOf(short): Short") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(S)Ljava/lang/Short;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Short.TYPE)
                methodCallStatement.returnType shouldBe Short::class.java
            }

            scenario("Boolean.valueOf(boolean): Boolean") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(Z)Ljava/lang/Boolean;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Boolean.TYPE)
                methodCallStatement.returnType shouldBe Boolean::class.java
            }

            scenario("Byte.valueOf(byte): Byte") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(B)Ljava/lang/Byte;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Byte.TYPE)
                methodCallStatement.returnType shouldBe Byte::class.java
            }

            scenario("Character.valueOf(char): Character") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(C)Ljava/lang/Character;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Character.TYPE)
                methodCallStatement.returnType shouldBe Character::class.java
            }

            scenario("Long.valueOf(long): Long") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(J)Ljava/lang/Long;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Long.TYPE)
                methodCallStatement.returnType shouldBe Long::class.java
            }

            scenario("Float.valueOf(float): Float") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(F)Ljava/lang/Float;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Float.TYPE)
                methodCallStatement.returnType shouldBe Float::class.java
            }

            scenario("Double.valueOf(double): Double") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "com.github.vilmosnagy.elq.elqcore.service.opcode.PrimitiveTestClass", "valueOf",
                        "(D)Ljava/lang/Double;", listOf()
                )

                methodCallStatement.targetClass shouldBe PrimitiveTestClass::class.java
                methodCallStatement.targetMethod shouldBe PrimitiveTestClass::class.java.getDeclaredMethod("valueOf", Double.TYPE)
                methodCallStatement.returnType shouldBe Double::class.java
            }
        }

        feature("Should return correct method call statement based on method signarute") {
            scenario("new Integer().intValue(): int") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokevirtual(0, 2), "java.lang.Integer", "intValue",
                        "()I", listOf()
                )

                methodCallStatement.targetClass shouldBe Integer::class.java
                methodCallStatement.targetMethod shouldBe Integer::class.java.getDeclaredMethod("intValue")
                methodCallStatement.returnType shouldBe Integer.TYPE
            }

            scenario("Arrays.<T>asList(T.. any): List<T>") {
                val methodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokevirtual(0, 2), "java.util.Arrays", "asList",
                        "([Ljava/lang/Object;)Ljava/util/List", listOf()
                )

                methodCallStatement.targetClass shouldBe Arrays::class.java
                methodCallStatement.targetMethod shouldBe Arrays::class.java.getDeclaredMethod("asList", Array<Any>::class.java)
                methodCallStatement.returnType shouldBe java.util.List::class.java
            }
        }

        feature("Should parse special methods by special call handlers") {
            scenario("Boolean.valueOf(false)") {
                val booleanClass = Boolean::class.java
                val booleanValueOf = booleanClass.getDeclaredMethod("valueOf", Boolean.TYPE)
                val methodCall = MethodCall(INVOKE_STATIC, booleanClass, booleanValueOf)
                val mockedMethodCallStatement = MethodCallStatement(targetClass = booleanClass, targetMethod = booleanValueOf, returnType = booleanClass, evaluatedStatement = Statement.LoadConstant(false))
                whenever(methodCallHandlerService.requiresSpecialHandle(methodCall)).thenReturn(true)
                whenever(methodCallHandlerService.handleSpecialMethodCall(methodCall, listOf(Statement.LoadConstant(0)))).thenReturn(mockedMethodCallStatement)

                val actualMethodCallStatement = testObj.parseInvokeOpCode(
                        OpCode.invokestatic(0, 0), "java.lang.Boolean", "valueOf",
                        "(Z)Ljava/lang/Boolean", listOf(Statement.LoadConstant(0))
                )

                actualMethodCallStatement shouldBe mockedMethodCallStatement
            }
        }
    }

}