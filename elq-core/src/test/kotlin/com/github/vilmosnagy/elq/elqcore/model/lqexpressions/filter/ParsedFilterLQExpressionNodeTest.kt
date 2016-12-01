package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter

import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.LogicalType
import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
class ParsedFilterLQExpressionNodeTest : FeatureSpec() {

    @Mock
    private lateinit var leftChild: ExpressionNode<TestEntity>

    @Mock
    private lateinit var rightChild: ExpressionNode<TestEntity>

    @Mock
    private lateinit var expressionBuilder: ExpressionBuilder<Any>

    init {
        MockitoAnnotations.initMocks(this)
        feature("Expression builder method should work correctly") {
            scenario("`AND` node should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionNode(leftChild, rightChild, LogicalType.AND)

                val lhs: Any = mock()
                val rhs: Any = mock()
                val predicate: Predicate<TestEntity> = mock()
                val lhsAndRhs: Any = mock()

                whenever(leftChild.buildExpression(expressionBuilder, predicate)).thenReturn(lhs)
                whenever(rightChild.buildExpression(expressionBuilder, predicate)).thenReturn(rhs)
                whenever(expressionBuilder.and(lhs, rhs)).thenReturn(lhsAndRhs)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe lhsAndRhs
            }

            scenario("`OR` node should build the correct Expression with ExpressionBuilder") {
                val testObj = ParsedFilterLQExpressionNode(leftChild, rightChild, LogicalType.OR)

                val lhs: Any = mock()
                val rhs: Any = mock()
                val predicate: Predicate<TestEntity> = mock()
                val lhsOrRhs: Any = mock()

                whenever(leftChild.buildExpression(expressionBuilder, predicate)).thenReturn(lhs)
                whenever(rightChild.buildExpression(expressionBuilder, predicate)).thenReturn(rhs)
                whenever(expressionBuilder.or(lhs, rhs)).thenReturn(lhsOrRhs)

                val actual = testObj.buildExpression(expressionBuilder, predicate)
                actual shouldBe lhsOrRhs
            }
        }
    }
}