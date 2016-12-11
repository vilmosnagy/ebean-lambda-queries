package com.github.vilmosnagy.elq.elqcore.model.lqexpressions.field

import io.kotlintest.specs.FeatureSpec

/**
 * @author Vilmos Nagy  <vilmos.nagy></vilmos.nagy>@outlook.com>
 */
class FieldReferenceTest : FeatureSpec() {

    init {
        feature("Should provide correct field references") {
            scenario("When simple field referenced") {
                val testObj = FieldReference(Any::class.java, "someField", null)
                val actualFieldRef = testObj.fullReference
                actualFieldRef shouldBe "someField"
            }

            scenario("When multiple field referenced trough getters") {
                val testObj = FieldReference(Any::class.java, "detailField", FieldReference(Any::class.java, "someAttribute", null))
                val actualFieldRef = testObj.fullReference
                actualFieldRef shouldBe "detailField.someAttribute"
            }
        }
    }
}