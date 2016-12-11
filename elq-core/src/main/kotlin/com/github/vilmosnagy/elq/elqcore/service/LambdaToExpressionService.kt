package com.github.vilmosnagy.elq.elqcore.service

import com.github.vilmosnagy.elq.elqcore.cache.*
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.isNonStatic
import com.github.vilmosnagy.elq.elqcore.isStatic
import com.github.vilmosnagy.elq.elqcore.model.Method
import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.field.FieldReference
import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter.ExpressionNode
import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter.ParsedFilterLQExpressionLeaf
import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.filter.ParsedFilterLQExpressionNode
import com.github.vilmosnagy.elq.elqcore.model.statements.GetFieldStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.MethodCallStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.BranchedStatement
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.CompareType
import com.github.vilmosnagy.elq.elqcore.model.statements.branch.LogicalType
import com.github.vilmosnagy.elq.elqcore.model.statements.kotlin.ThrowUninitializedPropertyAccessException
import java.io.Serializable
import java.lang.invoke.SerializedLambda
import javax.inject.Inject
import javax.inject.Singleton
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
@Singleton
internal class LambdaToExpressionService
@Inject constructor(
        private val methodParser: MethodParser,
        private val propertyService: JavaPropertyService
) {

    fun <T> parseFilterMethod(predicate: Predicate<T>, entityClazz: Class<T>): ExpressionNode<T> {
        val (predicateClass, filterMethod) = getPredicateClassAndFilterMethod(entityClazz, predicate)
        val method = methodParser.parseMethod(predicateClass, filterMethod)
        val evaluatedReturn = method.returnStatement.evaluate()
        return when (evaluatedReturn) {
            is BranchedStatement -> parseBranchedStatementToFilter(evaluatedReturn, entityClazz).component1()
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

    private fun <T> parseBranchedStatementToFilter(returnStatement: BranchedStatement, entityClazz: Class<T>): Pair<ExpressionNode<T>, Boolean> {
        return if (simpleTrueFalseBranchOnConstantEqualsField(returnStatement, entityClazz)) {
            parseSimpleTrueFalseBranch(entityClazz, returnStatement)
        } else if (multiLevelTrueFalseBranch(returnStatement)) {
            parseMultiLevelTrueFalseBranch(entityClazz, returnStatement)
        } else {
            val compareStatement = returnStatement.compareStatement
            val v1Eval = compareStatement.v1.evaluate()
            val v2Eval = compareStatement.v2.evaluate()
            if (v1Eval is BranchedStatement && v2Eval is Int) {
                if (v2Eval == 0) {
                    val copiedBranch = v1Eval.copy()
                    parseBranchedStatementToFilter(copiedBranch, entityClazz)
                } else {
                    TODO()
                }
            } else {
                TODO()
            }
        }
    }

    private fun <T> parseMultiLevelTrueFalseBranch(entityClazz: Class<T>, returnStatement: BranchedStatement): Pair<ParsedFilterLQExpressionNode<T>, Boolean> {
        val branch01 = returnStatement.branch01
        val branch02 = returnStatement.branch02
        val secondLevelBranchedStatement = branch01 as? BranchedStatement ?: branch02 as? BranchedStatement ?: TODO()
        val (leaf01, leaf01Value) = parseSimpleTrueFalseBranch(entityClazz, returnStatement)
        val (leaf02, leaf02Value) = parseBranchedStatementToFilter(secondLevelBranchedStatement, entityClazz)
        val logicalType = if (leaf01Value) LogicalType.OR else LogicalType.AND
        return Pair(ParsedFilterLQExpressionNode(leaf01, leaf02, logicalType), leaf01Value)
    }

    private fun <T> parseSimpleTrueFalseBranch(entityClazz: Class<T>, returnStatement: BranchedStatement): Pair<ParsedFilterLQExpressionLeaf<T>, Boolean> {
        val (compareType, branch01Value) = getCompareTypeAndNegateIfNecessary(returnStatement)
        val v1 = returnStatement.compareStatement.v1
        val v2 = returnStatement.compareStatement.v2
        val (fieldReference, fieldReferencingBranch) = getFieldReferenceFromComparedValues(v1, v2, entityClazz) ?: TODO()
        val rhsValue = if (fieldReferencingBranch == v1) v2 else v1
        // val (parsedFieldGetterMethod, propertyName) = getFieldReferenceFromGetterMethod(fieldGetterMethod)
        val evaluatedRhsValue = evaluateRhsStatement<T>(fieldReference, rhsValue)
        return Pair(ParsedFilterLQExpressionLeaf(fieldName = fieldReference.fullReference, value = evaluatedRhsValue, compareType = compareType), branch01Value)
    }

    private fun multiLevelTrueFalseBranch(returnStatement: BranchedStatement): Boolean {
        val branch01 = returnStatement.branch01
        val branch02 = returnStatement.branch02
        return if (branch01.evaluate() is Boolean && branch02 is BranchedStatement) {
            simpleTrueFalseBranch(branch02)
        } else if (branch02.evaluate() is Boolean && branch01 is BranchedStatement) {
            simpleTrueFalseBranch(branch01)
        } else {
            false
        }
    }

    private fun getCompareTypeAndNegateIfNecessary(returnStatement: BranchedStatement): Pair<CompareType, Boolean> {
        return if (returnStatement.branch01.evaluate() == true || returnStatement.branch02.evaluate() == false) {
            Pair(returnStatement.compareStatement.compareType.negate(), false)
        } else {
            Pair(returnStatement.compareStatement.compareType, true)
        }
    }

    private fun <T> simpleTrueFalseBranchOnConstantEqualsField(returnStatement: BranchedStatement, entityClazz: Class<T>): Boolean {
        return simpleTrueFalseBranch(returnStatement)
                && getFieldReferenceFromComparedValues(returnStatement.compareStatement.v1, returnStatement.compareStatement.v2, entityClazz) != null
    }

    private fun simpleTrueFalseBranch(returnStatement: BranchedStatement) = (listOf(returnStatement.branch01.evaluate(), returnStatement.branch02.evaluate()).containsAll(listOf(true, false))
            && returnStatement.branch01.evaluate() != returnStatement.branch02.evaluate())

    private fun <T> evaluateRhsStatement(fieldReference: FieldReference, rhsValue: Statement): ValueProvider<Predicate<T>> {
        val evaluatedRhsValue: ValueProvider<Predicate<T>> = when (rhsValue) {
            is Statement.LoadConstant<*> -> ConstantValueProvider<Predicate<T>>(rhsValue.value)
            is Statement.LoadVariable -> captureVariableFromMethodCall<T>(rhsValue, fieldReference)
            is GetFieldStatement -> FieldValueProvider(rhsValue.fieldName)
            is MethodCallStatement<*> -> evaluateMethodCall(rhsValue, fieldReference)
            else -> TODO()
        }
        return evaluatedRhsValue
    }

    private fun <T> evaluateMethodCall(rhsValue: MethodCallStatement<*>, fieldReference: FieldReference): ValueProvider<Predicate<T>> {
        val parameters = rhsValue
                .parameters
                .map { p -> evaluateRhsStatement<T>(fieldReference, p) }

        return if (rhsValue.targetMethod.isStatic) {
            MethodReturnValueProvider(rhsValue.targetMethod, parameters)
        } else {
            TODO()
        }
    }

    private fun <T> captureVariableFromMethodCall(rhsValue: Statement.LoadVariable, fieldReference: FieldReference): ValueProvider<Predicate<T>> {
//        val propertyCallChain = listOf(fieldGetterMethodCall.targetMethod, getMethodOfClass(Object::class.java, "equals", Object::class.java))
//        return MethodParameterValueProvider(rhsValue.variableIndex, propertyCallChain)
        val propertyList = propertyService.parsePropertyChainToGetters(fieldReference)
        // TODO not just equals, but probably all methods of the last property...
        val lastProperty = propertyList.last()
        val propertyListWithEquals = propertyList.toMutableList() + getMethodOfClass(lastProperty.returnType, "equals", Object::class.java)
        return MethodParameterValueProvider(rhsValue.variableIndex, propertyListWithEquals)
    }

    private fun <T> getFieldReferenceFromComparedValues(v1: Statement, v2: Statement, entityClazz: Class<T>): Pair<FieldReference, Statement>? {
        if (v1 is MethodCallStatement<*>) {
            val fieldRef = getReferencingFieldOfClass(v1, entityClazz)
            if (fieldRef != null) return Pair(fieldRef, v1)
        }
        if (v2 is MethodCallStatement<*>) {
            val fieldRef = getReferencingFieldOfClass(v2, entityClazz)
            if (fieldRef != null) return Pair(fieldRef, v2)
        }
        return null
    }

    // TODO cleanup this mess
    private fun <T> getReferencingFieldOfClass(methodCall: MethodCallStatement<*>, entityClazz: Class<T>): FieldReference? {
        val method = methodCall.value
        val evaluated = method.returnStatement.deepEvaluate()
        return if (evaluated is GetFieldStatement && evaluated.javaClass == entityClazz) {
            // TODO
            FieldReference(entityClazz, evaluated.fieldName, null)
        } else if (evaluated is BranchedStatement) {
            // TODO
            val withoutLateInit = evaluated.evaluateIfStraightForward().evaluate()
            if (withoutLateInit is GetFieldStatement) FieldReference(entityClazz, withoutLateInit.fieldName, null) else null
        // TODO
        } else if (method.returnStatement is Statement.ReturnStatement<*> && method.returnStatement.value is MethodCallStatement<*> && methodCall.targetMethod.isNonStatic && methodCall.parameters.size == 1 && methodCall.parameters[0].deepEvaluate() is GetFieldStatement) {
            val field = methodCall.parameters[0].deepEvaluate() as GetFieldStatement
            FieldReference(field.javaClass, field.fieldName, getReferencingFieldOfClass(method.returnStatement.value, method.returnStatement.value.targetClass))
        } else if (evaluated is MethodCallStatement<*>) {
            getReferencingFieldOfClass(evaluated, entityClazz)
        } else {
            null
        }
    }

}

