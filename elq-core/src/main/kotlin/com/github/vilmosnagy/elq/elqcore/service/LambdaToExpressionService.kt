package com.github.vilmosnagy.elq.elqcore.service

import com.avaje.ebean.Expression
import com.github.vilmosnagy.elq.elqcore.cache.*
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.model.Method
import com.github.vilmosnagy.elq.elqcore.model.statements.GetFieldStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.model.statements.kotlin.ThrowUninitializedPropertyAccessException
import java.io.Serializable
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Modifier
import javax.inject.Inject
import javax.inject.Singleton
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Singleton
class LambdaToExpressionService
@Inject constructor(
        private val methodParser: MethodParser
) {

    fun <T> parseFilterMethod(predicate: Predicate<T>, entityClazz: Class<T>): ParsedFilterLambdaDetails<T> {
        val (predicateClass, filterMethod) = getPredicateClassAndFilterMethod(entityClazz, predicate)
        val method = methodParser.parseMethod(predicateClass, filterMethod)
        val evaluatedReturn = method.returnStatement.evaluate()
        return when (evaluatedReturn) {
            is BranchedStatement -> parseBranchedStatementToFilter(evaluatedReturn, entityClazz)
            else -> TODO()
        }
    }

    private fun <T> getPredicateClassAndFilterMethod(entityClazz: Class<T>, predicate: Predicate<T>): Pair<Class<out Any>, java.lang.reflect.Method> {
        return if (predicate.javaClass.isSynthetic) {
            val serializedLambda = getSerializedLambda(predicate)
            val callerClass = Class.forName(serializedLambda.implClass.replace('/', '.'))
            callerClass to findMethodWithName(callerClass, serializedLambda.implMethodName)
        } else {
            predicate.javaClass to getMethodOfClass(predicate.javaClass, "test", entityClazz)
        }
    }

    private fun getMethodOfClass(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>) = clazz.getDeclaredMethod(methodName, *parameterTypes)

    private fun findMethodWithName(callerClass: Class<*>, methodName: String): java.lang.reflect.Method {
        // TODO method overloading.
        return callerClass.declaredMethods.first { it.name == methodName }
    }

    fun getSerializedLambda(lambda: Serializable): SerializedLambda {
        val method = lambda.javaClass.getDeclaredMethod("writeReplace")
        method.isAccessible = true
        return method.invoke(lambda) as SerializedLambda
    }

    private fun <T> parseBranchedStatementToFilter(returnStatement: BranchedStatement, entityClazz: Class<T>): ParsedFilterLambdaDetails<T> {
        return if (returnStatement.branch01.evaluate() == true && returnStatement.branch02.evaluate() == false
                && returnStatement.compareStatement.compareType == CompareType.NOT_EQUALS) {
            parseEqualsBranchesToFilter(returnStatement.compareStatement.v1, returnStatement.compareStatement.v2, entityClazz)
        } else {
            TODO()
        }
    }

    private fun <T> parseEqualsBranchesToFilter(v1: Statement, v2: Statement, entityClazz: Class<T>): ParsedFilterLambdaDetails<T> {
        val fieldReference = getFieldReferenceFromComparedValues(v1, v2, entityClazz)
        val otherReference = if (fieldReference == v1) v2 else v1
        return parseFieldEqualsExpression(fieldReference, otherReference)
    }

    private fun <T> parseFieldEqualsExpression(fieldGetterMethodCall: MethodCallStatement<*>, rhsValue: Statement): ParsedFilterLambdaDetails<T> {
        val (parsedGetterMethod, propertyName) = getFieldReferenceFromGetterMethod(fieldGetterMethodCall)
        val evaluatedRhsValue = evaluateRhsStatement<T>(fieldGetterMethodCall, parsedGetterMethod, rhsValue)
        return ParsedFilterLambdaDetails(fieldName = propertyName, value = evaluatedRhsValue)
    }

    private fun <T> evaluateRhsStatement(fieldGetterMethodCall: MethodCallStatement<*>, parsedGetterMethod: Method, rhsValue: Statement): ValueProvider<Predicate<T>> {
        val evaluatedRhsValue: ValueProvider<Predicate<T>> = when (rhsValue) {
            is Statement.LoadConstant<*> -> ConstantValueProvider<Predicate<T>>(rhsValue.value)
            is Statement.LoadVariable -> captureVariableFromMethodCall<T>(rhsValue, fieldGetterMethodCall, parsedGetterMethod)
            is GetFieldStatement -> FieldValueProvider(rhsValue.fieldName)
            is MethodCallStatement<*> -> evaluateMethodCall(rhsValue, fieldGetterMethodCall, parsedGetterMethod)
            else -> TODO()
        }
        return evaluatedRhsValue
    }

    private fun <T> evaluateMethodCall(rhsValue: MethodCallStatement<*>, fieldGetterMethodCall: MethodCallStatement<*>, parsedGetterMethod: Method): ValueProvider<Predicate<T>> {
        val parameters = rhsValue
                .parameters
                .map { p -> evaluateRhsStatement<T>(fieldGetterMethodCall, parsedGetterMethod, p) }

        return if (rhsValue.targetMethod.isStatic) {
            MethodReturnValueProvider(rhsValue.targetMethod, null, parameters)
        } else {
            TODO()
        }
    }

    private fun getFieldReferenceFromGetterMethod(fieldGetterMethodCall: MethodCallStatement<*>): Pair<Method, String> {
        val parsedGetterMethod = methodParser.parseMethod(fieldGetterMethodCall.targetClass, fieldGetterMethodCall.targetMethod)
        val getterMethodBody = if (parsedGetterMethod.returnStatement is Statement.EvaluableStatement<*>) {
            methodParser.unravelMethodCallChain(parsedGetterMethod.returnStatement).value
        } else if (parsedGetterMethod.returnStatement is BranchedStatement) {
            methodParser.unravelMethodCallChain(handleBranchedStatementAsGetterMethodReturnValue(parsedGetterMethod.returnStatement)).value
        } else {
            TODO()
        }

        val propertyName = when (getterMethodBody) {
            is GetFieldStatement -> getterMethodBody.fieldName
            else -> TODO()
        }
        return Pair(parsedGetterMethod, propertyName)
    }

    private fun handleBranchedStatementAsGetterMethodReturnValue(returnStatement: BranchedStatement): Statement.EvaluableStatement<*> {
        val branch01 = returnStatement.branch01
        val branch02 = returnStatement.branch02
        return if (branch01.evaluate() == ThrowUninitializedPropertyAccessException || branch02.evaluate() == ThrowUninitializedPropertyAccessException) {
            (if (branch01.evaluate() == ThrowUninitializedPropertyAccessException) branch02 else branch01) as Statement.EvaluableStatement<*>
        } else {
            TODO()
        }
    }

    private fun <T> captureVariableFromMethodCall(rhsValue: Statement.LoadVariable, fieldGetterMethodCall: MethodCallStatement<*>, parsedGetterMethod: Method): ValueProvider<Predicate<T>> {
        val propertyCallChain = listOf(fieldGetterMethodCall.targetMethod, getMethodOfClass(parsedGetterMethod.jvmMethod.returnType, "equals", Object::class.java))
        return MethodParameterValueProvider(rhsValue.variableIndex, propertyCallChain)
    }

    private fun <T> getFieldReferenceFromComparedValues(v1: Statement, v2: Statement, entityClazz: Class<T>): MethodCallStatement<*> {
        return if (v1 is MethodCallStatement<*> && v1.targetClass == entityClazz) {
            v1
        } else if (v2 is MethodCallStatement<*> && v2.targetClass == entityClazz) {
            v2
        } else {
            TODO()
        }
    }

}

private val java.lang.reflect.Method.isStatic: Boolean
    get() = Modifier.isStatic(modifiers)

data class ParsedFilterLambdaDetails<T> (
        val fieldName: String,
        val value: ValueProvider<Predicate<T>>
)
