package com.github.vilmosnagy.elq.elqcore.dagger

import com.avaje.ebean.Expression
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.service.LambdaToExpressionService
import com.github.vilmosnagy.elq.elqcore.service.MethodParser
import dagger.Component
import javax.inject.Singleton


/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Singleton
@Component(modules = arrayOf(InterfaceModule::class))
internal interface AppCtx {

    val filterMethodParser: LambdaToExpressionService
    val methodParser: MethodParser
    val expressionBuilder: ExpressionBuilder<Expression>

    companion object {
        val get: AppCtx
            get() = getAppCtx()

        private fun getAppCtx(): AppCtx {
            val mockedAppCtx = this.mockedAppCtx
            if (mockedAppCtx != null) {
                return mockedAppCtx
            } else {
                return DaggerAppCtx.builder().build()
            }
        }

        internal var mockedAppCtx: AppCtx? = null
    }

}