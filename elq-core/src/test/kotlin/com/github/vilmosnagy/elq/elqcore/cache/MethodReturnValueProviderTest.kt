package com.github.vilmosnagy.elq.elqcore.cache

import com.github.vilmosnagy.elq.elqcore.KotlinTestRunner
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import java.util.*

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
@RunWith(KotlinTestRunner::class)
class MethodReturnValueProviderTest : FeatureSpec() {

    init {
        MockitoAnnotations.initMocks(this)
        feature("Methods' return value should be calculated correctly") {
            scenario("Static method's return value should be calculated correctly") {
                val uuidFromStringMethod = UUID::class.java.getDeclaredMethod("fromString", java.lang.String::class.java)
                val parameterProvider: ValueProvider<TestPredicate> = mock()
                val testPredicate: TestPredicate = mock()
                val randomUUID = UUID.randomUUID()

                val testObj = MethodReturnValueProvider(uuidFromStringMethod, listOf(parameterProvider))

                whenever(parameterProvider.getValue(testPredicate)).thenReturn(randomUUID.toString())

                val actual = testObj.getValue(testPredicate)
                actual shouldBe randomUUID
            }
        }
    }
}

internal class TestPredicate