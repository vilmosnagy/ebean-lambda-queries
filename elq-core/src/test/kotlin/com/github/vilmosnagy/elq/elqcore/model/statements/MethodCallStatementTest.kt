package com.github.vilmosnagy.elq.elqcore.model.statements

import com.github.vilmosnagy.elq.elqcore.dagger.AppCtx
import com.github.vilmosnagy.elq.elqcore.model.Method
import com.github.vilmosnagy.elq.elqcore.service.MethodParser
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class MethodCallStatementTest: FeatureSpec() {

    open class A {
        open fun foo(): Boolean = true
    }

    @Mock
    private lateinit var appCtx: AppCtx

    @Mock
    private lateinit var methodParser: MethodParser

    init{
        MockitoAnnotations.initMocks(this)
        AppCtx.mockedAppCtx = appCtx

        whenever(appCtx.methodParser).thenReturn(methodParser)

        feature("Should be able to get value from `MethodCallStatement`") {
            val targetClass = A::class.java
            val targetMethod = A::class.java.getDeclaredMethod("foo")
            scenario("Should return pre-evaluated value as methodCall's value") {
                val expectedValue = Statement.LoadConstant(15)
                val testObj = MethodCallStatement(
                        targetClass = targetClass,
                        targetMethod = targetMethod,
                        returnType = Boolean::class.java,
                        evaluatedStatement = expectedValue
                )
                val (actualMethod, actualValue) = testObj.value
                actualValue shouldBe expectedValue
                actualMethod shouldBe targetMethod
            }

            scenario("Should return value when it has to evaluate method body as methodCall's value") {
                val parsedMethod: Method = Method(targetMethod, Statement.LoadConstant(1))
                val testObj = MethodCallStatement(
                        targetClass = A::class.java,
                        targetMethod = A::class.java.getDeclaredMethod("foo"),
                        returnType = Boolean::class.java
                )

                whenever(methodParser.parseMethod(A::class.java, targetMethod)).thenReturn(parsedMethod)

                val actualValue = testObj.value
                actualValue shouldBe parsedMethod
            }
        }
    }
}