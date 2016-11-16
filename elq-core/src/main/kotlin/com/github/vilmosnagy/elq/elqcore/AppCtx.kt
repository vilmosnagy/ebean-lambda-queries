package com.github.vilmosnagy.elq.elqcore

import com.github.vilmosnagy.elq.elqcore.service.ExpressionBuilderService
import com.github.vilmosnagy.elq.elqcore.service.LambdaToExpressionService
import com.github.vilmosnagy.elq.elqcore.service.MethodParser
import dagger.Component
import javax.inject.Singleton


/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
@Singleton
@Component
interface AppCtx {

    val filterMethodParser: LambdaToExpressionService
    val methodParser: MethodParser
    val expressionBuilder: ExpressionBuilderService

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