package com.github.vilmosnagy.elq.elqcore.cache

import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FeatureSpec
import org.junit.Assert.*
import org.mockito.MockitoAnnotations
import java.util.*

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
class MethodReturnValueProviderTest : FeatureSpec() {

    private open class TestPredicate

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