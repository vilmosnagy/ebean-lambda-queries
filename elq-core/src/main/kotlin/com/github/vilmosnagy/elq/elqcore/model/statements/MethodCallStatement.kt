package com.github.vilmosnagy.elq.elqcore.model.statements

import com.github.vilmosnagy.elq.elqcore.AppCtx
import com.github.vilmosnagy.elq.elqcore.model.Method
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
data class MethodCallStatement<out T> (
        val targetClass: Class<*>,
        val targetMethod: java.lang.reflect.Method,
        val parameters: List<Statement> = listOf(),
        val returnType: Class<out T>,
        private val evaluatedStatement: Statement? = null
): Statement.EvaluableStatement<Method>, Statement.LazyEvaluatedStatement<Method> {

    override val value: Method by lazy {
        if (evaluatedStatement != null) {
            Method(targetMethod, evaluatedStatement)
        } else {
            AppCtx.get.methodParser.parseMethod(targetClass, targetMethod)
        }
    }

}