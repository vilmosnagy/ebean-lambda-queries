package com.github.vilmosnagy.elq.elqcore.cache

import com.github.vilmosnagy.elq.elqcore.KotlinTestRunner
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import io.kotlintest.specs.FeatureSpec
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@RunWith(KotlinTestRunner::class)
class MethodParameterValueProviderTest : FeatureSpec() {

    open class Bar {
        open fun notCapturedMethod() = Unit
        open val foo = Foo()
    }

    open class Foo {
        open fun notCapturedMethod() = Unit
        open fun capturedMethod(bar: Any) = true
    }

    init {
        MockitoAnnotations.initMocks(this)

        feature("Should intercept argument from not chained method calls") {
            val propertyCallChain = listOf(Foo::class.java.getDeclaredMethod("capturedMethod", Any::class.java))
            scenario("should capture simple methods first argument") {
                val realArgument = "Some string parameter"
                val predicate: Predicate<Foo> = Predicate() { it.capturedMethod(realArgument) }
                val testObj = MethodParameterValueProvider<Foo>(0, propertyCallChain)
                val capturedArgument = testObj.getValue(predicate)
                capturedArgument shouldBe realArgument
            }

            scenario("should capture simple methods first argument when predicate contains other method calls than the captured one") {
                val realArgument = "Some string parameter"
                val predicate: Predicate<Foo> = Predicate() {
                    it.notCapturedMethod()
                    it.capturedMethod(realArgument)
                }
                val testObj = MethodParameterValueProvider<Foo>(0, propertyCallChain)
                val capturedArgument = testObj.getValue(predicate)
                capturedArgument shouldBe realArgument
            }
        }

        feature("Should intercept argument chained method calls") {
            val propertyCallChain = listOf(Bar::class.java.getDeclaredMethod("getFoo"), Foo::class.java.getDeclaredMethod("capturedMethod", Any::class.java))
            scenario("should capture simple methods first argument") {
                val realArgument = "Some string parameter"
                val predicate: Predicate<Bar> = Predicate() { it.foo.capturedMethod(realArgument) }
                val testObj = MethodParameterValueProvider<Bar>(0, propertyCallChain)
                val capturedArgument = testObj.getValue(predicate)
                capturedArgument shouldBe realArgument
            }

            scenario("should capture simple methods first argument when predicate contains other method calls than the captured one") {
                val realArgument = "Some string parameter"
                val predicate: Predicate<Bar> = Predicate() {
                    it.notCapturedMethod()
                    it.foo.capturedMethod(realArgument)
                }
                val testObj = MethodParameterValueProvider<Bar>(0, propertyCallChain)
                val capturedArgument = testObj.getValue(predicate)
                capturedArgument shouldBe realArgument
            }
        }
    }
}