package com.github.vilmosnagy.elq.elqcore.stream

import com.avaje.ebean.Ebean
import com.avaje.ebean.Query
import com.ea.agentloader.AgentLoader
import com.ea.agentloader.ClassPathUtils
import com.github.vilmosnagy.elq.elqcore.AppCtx
import com.github.vilmosnagy.elq.elqcore.agent.JavaAgent
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.service.ExpressionBuilderService
import java.io.Serializable
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Method
import java.util.function.BiConsumer
import java.util.function.Supplier
import java.util.stream.Collector
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
class ElqStreamImpl<T>
private constructor(override val clazz: Class<T>, override val query: Query<T>) :
        ElqStreamKt<T> {

    private val filterMethodParser = AppCtx.get.filterMethodParser
    private val expressionBuilder = AppCtx.get.expressionBuilder

    internal constructor(clazz: Class<T>) : this(clazz, Ebean.find(clazz))

    override fun count(): Long {
        return query.findCount().toLong()
    }

    override fun <R, A> collect(collector: Collector<in T, A, R>): R = query.findList().stream().collect(collector)

    override fun filter(predicate: Predicate<T>): ElqStream<T> {
        val parsedLambdaDetails = filterMethodParser.parseFilterMethod(predicate, clazz)
        query.where(expressionBuilder.equals(parsedLambdaDetails.fieldName, parsedLambdaDetails.value.getValue(predicate)))
        return this
    }
}

fun <T> createElqStream(clazz: Class<T>): ElqStream<T> {
    return ElqStreamImpl(clazz)
}


//   if (ClassLoader.getSystemClassLoader() !== JavaAgent::class.java.getClassLoader()) {
//      ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(JavaAgent::class.java))
//   }
//   AgentLoader.loadAgentClass(JavaAgent::class.java.name, null)