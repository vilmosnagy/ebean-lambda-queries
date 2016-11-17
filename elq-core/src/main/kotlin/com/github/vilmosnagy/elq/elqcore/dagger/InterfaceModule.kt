package com.github.vilmosnagy.elq.elqcore.dagger

import com.avaje.ebean.Expression
import com.github.vilmosnagy.elq.elqcore.interfaces.ExpressionBuilder
import com.github.vilmosnagy.elq.elqcore.service.EbeanExpressionBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
@Module
@Singleton
internal class InterfaceModule {

    @Singleton
    @Provides
    fun getEbeanExpressionBuilder(): ExpressionBuilder<Expression> {
        return EbeanExpressionBuilder()
    }
}