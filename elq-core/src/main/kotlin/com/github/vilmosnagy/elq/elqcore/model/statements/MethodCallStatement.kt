package com.github.vilmosnagy.elq.elqcore.model.statements

import com.github.vilmosnagy.elq.elqcore.dagger.AppCtx
import com.github.vilmosnagy.elq.elqcore.model.Method
import java.lang.reflect.Method as JVMMethod

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class MethodCallStatement<out T>(
        internal val targetClass: Class<*>,
        internal val targetMethod: java.lang.reflect.Method,
        internal val parameters: List<Statement> = listOf(),
        internal val returnType: Class<out T>,
        private val evaluatedStatement: Statement? = null
) : Statement.EvaluableStatement<Method>, Statement.LazyEvaluatedStatement {

    override val value: Method by lazy {
        if (evaluatedStatement != null) {
            Method(targetMethod, evaluatedStatement)
        } else {
            AppCtx.get.methodParser.parseMethod(targetClass, targetMethod)
        }
    }

}