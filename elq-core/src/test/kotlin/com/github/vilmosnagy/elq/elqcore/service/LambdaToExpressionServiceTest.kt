@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.vilmosnagy.elq.elqcore.service

import com.github.vilmosnagy.elq.elqcore.cache.ConstantValueProvider
import com.github.vilmosnagy.elq.elqcore.cache.MethodParameterValueProvider
import com.github.vilmosnagy.elq.elqcore.cache.MethodReturnValueProvider
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.FunctionAlbumIdEqualsFive
import com.github.vilmosnagy.elq.elqcore.model.Method
import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter.ParsedFilterLQExpressionLeaf
import com.github.vilmosnagy.elq.elqcore.model.statements.GetFieldStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class LambdaToExpressionServiceTest : FeatureSpec() {

    @Mock
    private lateinit var methodParser: MethodParser

    @Mock
    private lateinit var expressionBuilder: EbeanExpressionBuilder

    @InjectMocks
    private lateinit var testObj: LambdaToExpressionService

    init {
        MockitoAnnotations.initMocks(this)

        feature("Should parse filter method into ParsedFilterLQExpressionLeaf") {
            scenario("should transform parsed method (`getField().equals(constant)`) to (fieldName, value) pair") {
                val lambdaClass = FunctionAlbumIdEqualsFive::class.java
                val lambdaMethod = FunctionAlbumIdEqualsFive::class.java.getDeclaredMethod("test", TestEntity::class.java)
                val entityClass = TestEntity::class.java
                val entityIdGetterMethod = TestEntity::class.java.getDeclaredMethod("getId")

                val returnedStatement = BranchedStatement(
                        CompareStatement(
                                Statement.LoadConstant(5),
                                MethodCallStatement(
                                        entityClass,
                                        entityIdGetterMethod,
                                        listOf(),
                                        java.lang.Integer.TYPE, null
                                ),
                                CompareType.NOT_EQUALS
                        ),
                        Statement.ReturnStatement(Statement.LoadConstant(true)),
                        Statement.ReturnStatement(Statement.LoadConstant(false))
                )
                val getFieldStatement = GetFieldStatement(TestEntity::class.java, "id")
                val getterReturnStatement = Statement.ReturnStatement(getFieldStatement)

                val getterMethod = Method(entityIdGetterMethod, getterReturnStatement)
                val parsedMethod = Method(lambdaMethod, returnedStatement)

                whenever(methodParser.parseMethod(lambdaClass, lambdaMethod)).thenReturn(parsedMethod)
                whenever(methodParser.parseMethod(entityClass, entityIdGetterMethod)).thenReturn(getterMethod)
                whenever(methodParser.unravelMethodCallChain(getterReturnStatement)).thenReturn(getterReturnStatement)

                val actual = testObj.parseFilterMethod(FunctionAlbumIdEqualsFive(), TestEntity::class.java) as ParsedFilterLQExpressionLeaf
                actual.fieldName shouldBe "id"
                actual.value.getValue(FunctionAlbumIdEqualsFive()) shouldBe 5

                verify(methodParser).unravelMethodCallChain(getterReturnStatement)
            }

            scenario("should transform parsed method (`getField().equals(String.valueOf(constant))`) to (fieldName, value) pair") {
                val lambda: Predicate<TestEntity> = Predicate { it.title == java.lang.String.valueOf("Gordon Freeman") }

                val lambdaClass = lambda.javaClass
                val lambdaMethod = lambda.javaClass.getDeclaredMethod("test", TestEntity::class.java)
                val entityClass = TestEntity::class.java
                val entityTitleGetterMethod = TestEntity::class.java.getDeclaredMethod("getTitle")
                val stringValueOfMethod = java.lang.String::class.java.getDeclaredMethod("valueOf", Object::class.java)

                val returnedStatement = BranchedStatement(
                        CompareStatement(
                                MethodCallStatement(
                                        java.lang.String::class.java,
                                        stringValueOfMethod,
                                        listOf(Statement.LoadConstant("Gordon Freeman")),
                                        java.lang.String::class.java
                                ),
                                MethodCallStatement(
                                        entityClass,
                                        entityTitleGetterMethod,
                                        listOf(),
                                        java.lang.String::class.java
                                ),
                                CompareType.NOT_EQUALS
                        ),
                        Statement.ReturnStatement(Statement.LoadConstant(true)),
                        Statement.ReturnStatement(Statement.LoadConstant(false))
                )
                val getFieldStatement = GetFieldStatement(TestEntity::class.java, "title")
                val getterReturnStatement = Statement.ReturnStatement(getFieldStatement)

                val getterMethod = Method(entityTitleGetterMethod, getterReturnStatement)
                val parsedMethod = Method(lambdaMethod, returnedStatement)

                whenever(methodParser.parseMethod(lambdaClass, lambdaMethod)).thenReturn(parsedMethod)
                whenever(methodParser.parseMethod(entityClass, entityTitleGetterMethod)).thenReturn(getterMethod)
                whenever(methodParser.unravelMethodCallChain(getterReturnStatement)).thenReturn(getterReturnStatement)

                val actual = testObj.parseFilterMethod(lambda, TestEntity::class.java)
                actual shouldBe ParsedFilterLQExpressionLeaf<TestEntity>("title", MethodReturnValueProvider(stringValueOfMethod, null, listOf(ConstantValueProvider("Gordon Freeman"))), CompareType.EQUALS)

                verify(methodParser).unravelMethodCallChain(getterReturnStatement)
            }

            scenario("should transform parsed method (`getField().equals(variable)` - and capture variable in method call) to (fieldName, value) pair") {
                val date = Date()
                val lambda: Predicate<TestEntity> = Predicate { it.version == date }

                val lambdaClass = lambda.javaClass
                val lambdaMethod = lambda.javaClass.getDeclaredMethod("test", TestEntity::class.java)
                val entityClass = TestEntity::class.java
                val entityVersionGetterMethod = TestEntity::class.java.getDeclaredMethod("getVersion")

                val returnedStatement = BranchedStatement(
                        CompareStatement(
                                Statement.LoadVariable(1),
                                MethodCallStatement(
                                        entityClass,
                                        entityVersionGetterMethod,
                                        listOf(),
                                        Date::class.java
                                ),
                                CompareType.NOT_EQUALS
                        ),
                        Statement.ReturnStatement(Statement.LoadConstant(true)),
                        Statement.ReturnStatement(Statement.LoadConstant(false))
                )
                val getFieldStatement = GetFieldStatement(TestEntity::class.java, "version")
                val getterReturnStatement = Statement.ReturnStatement(getFieldStatement)

                val getterMethod = Method(entityVersionGetterMethod, getterReturnStatement)
                val parsedMethod = Method(lambdaMethod, returnedStatement)

                whenever(methodParser.parseMethod(lambdaClass, lambdaMethod)).thenReturn(parsedMethod)
                whenever(methodParser.parseMethod(entityClass, entityVersionGetterMethod)).thenReturn(getterMethod)
                whenever(methodParser.unravelMethodCallChain(getterReturnStatement)).thenReturn(getterReturnStatement)

                val actual = testObj.parseFilterMethod(lambda, TestEntity::class.java) as ParsedFilterLQExpressionLeaf
                actual.fieldName shouldBe "version"
                (actual.value as MethodParameterValueProvider<*>).variableIndex shouldBe 1
                (actual.value as MethodParameterValueProvider<*>).propertyCallChain shouldBe listOf(entityVersionGetterMethod, Date::class.java.getDeclaredMethod("equals", Object::class.java))

                verify(methodParser).unravelMethodCallChain(getterReturnStatement)
            }

            scenario("should transform parsed method (`getField().equals(field in anonym class)` and get field from anonym class) to (fieldName, value) pair") {
                val date = Date()
                val lambda: Predicate<TestEntity> = object : Predicate<TestEntity> {

                    val localDate = date

                    override fun test(t: TestEntity): Boolean {
                        return t.version == localDate
                    }

                }

                val lambdaClass = lambda.javaClass
                val lambdaMethod = lambda.javaClass.getDeclaredMethod("test", TestEntity::class.java)
                val entityClass = TestEntity::class.java
                val entityVersionGetterMethod = TestEntity::class.java.getDeclaredMethod("getVersion")

                val returnedStatement = BranchedStatement(
                        CompareStatement(
                                GetFieldStatement(lambdaClass, "localDate"),
                                MethodCallStatement(
                                        entityClass,
                                        entityVersionGetterMethod,
                                        listOf(),
                                        Date::class.java
                                ),
                                CompareType.NOT_EQUALS
                        ),
                        Statement.ReturnStatement(Statement.LoadConstant(true)),
                        Statement.ReturnStatement(Statement.LoadConstant(false))
                )
                val getFieldStatement = GetFieldStatement(TestEntity::class.java, "version")
                val getterReturnStatement = Statement.ReturnStatement(getFieldStatement)

                val getterMethod = Method(entityVersionGetterMethod, getterReturnStatement)
                val parsedMethod = Method(lambdaMethod, returnedStatement)

                whenever(methodParser.parseMethod(lambdaClass, lambdaMethod)).thenReturn(parsedMethod)
                whenever(methodParser.parseMethod(entityClass, entityVersionGetterMethod)).thenReturn(getterMethod)
                whenever(methodParser.unravelMethodCallChain(getterReturnStatement)).thenReturn(getterReturnStatement)

                val actual = testObj.parseFilterMethod(lambda, TestEntity::class.java) as ParsedFilterLQExpressionLeaf
                actual.fieldName shouldBe "version"
                actual.value.getValue(lambda) shouldBe date

                verify(methodParser).unravelMethodCallChain(getterReturnStatement)
            }
        }
    }
}