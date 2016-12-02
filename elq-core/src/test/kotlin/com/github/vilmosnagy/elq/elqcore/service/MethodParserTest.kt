package com.github.vilmosnagy.elq.elqcore.service

import com.github.vilmosnagy.elq.elqcore.model.Method
import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCode
import com.github.vilmosnagy.elq.elqcore.model.opcodes.OpCodeType
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.service.opcode.GeneralOpCodeParser
import com.nhaarman.mockito_kotlin.*

import io.kotlintest.specs.FeatureSpec
import org.apache.bcel.Repository
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class MethodParserTest : FeatureSpec() {

    @Mock
    private lateinit var generalOpCodeParser: GeneralOpCodeParser

    @InjectMocks
    private lateinit var testObj: MethodParser

    init {
        MockitoAnnotations.initMocks(this)

        val getVariableMethod = ClassWithSimpleGetter::class.java.getDeclaredMethod("getVariable")

        feature("Should parse method") {
            scenario("Should parse simple getter") {
                val bcelClass = Repository.lookupClass(ClassWithSimpleGetter::class.qualifiedName)
                val bcelMethod = bcelClass.getMethod(getVariableMethod)

                val methodParsedStatement: Statement = mock()

                doAnswer {
                    val opCodeList: List<OpCode> = it.getArgument(0)
                    opCodeList[0].type shouldBe OpCodeType.iconst_1
                    opCodeList[1].type shouldBe OpCodeType.invokestatic
                    opCodeList[2].type shouldBe OpCodeType.areturn
                    methodParsedStatement
                }.whenever(generalOpCodeParser).parseExpressionList(any(), eq(bcelClass), eqf(getVariableMethod), any())

                val parsedMethod = testObj.parseMethod(ClassWithSimpleGetter::class.java, getVariableMethod)

                verify(generalOpCodeParser).parseExpressionList(any(), eq(bcelClass), eqf(getVariableMethod), any())
                parsedMethod shouldBe Method(getVariableMethod, methodParsedStatement)
            }
        }

        feature("Should unravel method call") {
            scenario("When method call returns an evaluable statement") {
                val lastMethodCallsValue = Statement.ReturnStatement(Statement.LoadConstant(5))
                val lastMethodCallStatement = MethodCallStatement(
                        targetClass = ClassWithSimpleGetter::class.java,
                        targetMethod = getVariableMethod,
                        returnType = Any::class.java,
                        evaluatedStatement = Method(getVariableMethod, lastMethodCallsValue))

                val unraveledStatement = testObj.unravelMethodCallChain(Statement.ReturnStatement(lastMethodCallStatement))
                unraveledStatement.value shouldBe lastMethodCallsValue
            }
        }
    }
}

internal inline fun <reified T : Any> eqf(value: T): T = Mockito.eq(value) ?: value

open class ClassWithSimpleGetter {
    fun getVariable(): Any? {
        return 1
    }
}