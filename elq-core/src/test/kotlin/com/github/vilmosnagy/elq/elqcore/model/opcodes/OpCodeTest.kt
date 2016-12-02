package com.github.vilmosnagy.elq.elqcore.model.opcodes

import com.github.vilmosnagy.elq.elqcore.KotlinTestRunner
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.specs.FeatureSpec
import org.junit.runner.RunWith

/**
 * @author Vilmos Nagy  <vilmos.nagy></vilmos.nagy>@outlook.com>
 */
@RunWith(KotlinTestRunner::class)
class OpCodeTest : FeatureSpec() {

    init {
        feature("") {
            scenario("All OpCode instance should have the correct OpCodeType enum set as enum") {
                val fakeBytes = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                OpCodeType.values().forEach {
                    val opCode = it.createNew(fakeBytes, mock())
                    opCode.type shouldBe it
                }
            }
        }
    }

}