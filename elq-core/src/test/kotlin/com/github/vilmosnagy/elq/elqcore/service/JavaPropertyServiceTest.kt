package com.github.vilmosnagy.elq.elqcore.service

import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.field.FieldReference
import com.github.vilmosnagy.elq.elqcore.test.model.TestEntity
import io.kotlintest.specs.FeatureSpec
import org.junit.Assert.*
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations

/**
 * @author Vilmos Nagy  <vilmos.nagy></vilmos.nagy>@outlook.com>
 */
class JavaPropertyServiceTest : FeatureSpec() {

    @InjectMocks
    private lateinit var testObj: JavaPropertyService

    init {
        MockitoAnnotations.initMocks(this)
        feature("Should parse property chains - `a.b.c` - into getter method list") {
            scenario("Should parse simple property into singleton getter method") {
                val fieldReference = FieldReference(TestEntity::class.java, "version", null)
                val expectedGetter = TestEntity::class.java.getDeclaredMethod("getVersion")

                val actualList = testObj.parsePropertyChainToGetters(fieldReference)
                actualList shouldBe listOf(expectedGetter)
            }
        }
    }
}