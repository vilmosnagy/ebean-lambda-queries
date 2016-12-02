package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter

import com.github.vilmosnagy.elq.elqcore.cache.ValueProvider
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
class ParsedFilterLQExpressionLeafTest : FeatureSpec() {

    @Mock
    private lateinit var valueProvider: ValueProvider<Predicate<TestEntity>>

    @Mock
    private lateinit var expressionBuilder: ExpressionBuilder<Any>

    init {
        MockitoAnnotations.initMocks(this)
        feature("Expression builder method should work correctly") {
            scenario("`EQUALS` Leaf should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionLeaf("fieldName", valueProvider, CompareType.EQUALS)

                val predicate: Predicate<TestEntity> = mock()
                val value: Any = mock()
                val expression: Any = mock()

                whenever(valueProvider.getValue(predicate)).thenReturn(value)
                whenever(expressionBuilder.equals("fieldName", value)).thenReturn(expression)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe expression
            }

            scenario("`GREATER_THAN_OR_EQUALS` Leaf should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionLeaf("fieldName", valueProvider, CompareType.GREATER_THAN_OR_EQUALS)

                val predicate: Predicate<TestEntity> = mock()
                val value: Any = mock()
                val expression: Any = mock()

                whenever(valueProvider.getValue(predicate)).thenReturn(value)
                whenever(expressionBuilder.greaterThanOrEquals("fieldName", value)).thenReturn(expression)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe expression
            }

            scenario("`LESS_THAN` Leaf should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionLeaf("fieldName", valueProvider, CompareType.LESS_THAN)

                val predicate: Predicate<TestEntity> = mock()
                val value: Any = mock()
                val expression: Any = mock()

                whenever(valueProvider.getValue(predicate)).thenReturn(value)
                whenever(expressionBuilder.lessThan("fieldName", value)).thenReturn(expression)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe expression
            }

            scenario("`LESS_THAN_OR_EQUALS` Leaf should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionLeaf("fieldName", valueProvider, CompareType.LESS_THAN_OR_EQUALS)

                val predicate: Predicate<TestEntity> = mock()
                val value: Any = mock()
                val expression: Any = mock()

                whenever(valueProvider.getValue(predicate)).thenReturn(value)
                whenever(expressionBuilder.lessThanOrEquals("fieldName", value)).thenReturn(expression)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe expression
            }

            scenario("`GREATER_THAN` Leaf should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionLeaf("fieldName", valueProvider, CompareType.GREATER_THAN)

                val predicate: Predicate<TestEntity> = mock()
                val value: Any = mock()
                val expression: Any = mock()

                whenever(valueProvider.getValue(predicate)).thenReturn(value)
                whenever(expressionBuilder.greaterThan("fieldName", value)).thenReturn(expression)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe expression
            }

            scenario("`NOT_EQUALS` Leaf should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionLeaf("fieldName", valueProvider, CompareType.NOT_EQUALS)

                val predicate: Predicate<TestEntity> = mock()
                val value: Any = mock()
                val expression: Any = mock()

                whenever(valueProvider.getValue(predicate)).thenReturn(value)
                whenever(expressionBuilder.notEquals("fieldName", value)).thenReturn(expression)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe expression
            }
        }
    }
}