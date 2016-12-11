package com.github.vilmosnagy.elq.elqcore.model.statements.branch

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.specs.FeatureSpec
import org.mockito.Mockito

/**
 * @author Vilmos Nagy  <vilmos.nagy></vilmos.nagy>@outlook.com>
 */
class CompareStatementTest : FeatureSpec() {

    init {
        feature("`isNullCheck` returns correct value") {
            val statementThatEvaluatesSomething: Statement = mock(Mockito.RETURNS_DEEP_STUBS)
            scenario("(true, 0)") {
                CompareStatement(Statement.LoadConstant(null), statementThatEvaluatesSomething, CompareType.EQUALS).isNullCheck() shouldBe Pair(true, 0)
                CompareStatement(statementThatEvaluatesSomething, Statement.LoadConstant(null), CompareType.EQUALS).isNullCheck() shouldBe Pair(true, 0)
            }

            scenario("(true, 1)") {
                CompareStatement(Statement.LoadConstant(null), statementThatEvaluatesSomething, CompareType.NOT_EQUALS).isNullCheck() shouldBe Pair(true, 1)
                CompareStatement(statementThatEvaluatesSomething, Statement.LoadConstant(null), CompareType.NOT_EQUALS).isNullCheck() shouldBe Pair(true, 1)
            }

            scenario("(false, anything)") {
                val (bool, _int) = CompareStatement(statementThatEvaluatesSomething, statementThatEvaluatesSomething, CompareType.NOT_EQUALS).isNullCheck()
                 bool shouldBe false
            }
        }
    }
}