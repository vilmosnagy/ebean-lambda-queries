package com.github.vilmosnagy.elq.elqcore.stream

import com.avaje.ebean.Ebean
import com.avaje.ebean.Query
import com.github.vilmosnagy.elq.elqcore.AppCtx
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import java.util.stream.Collector

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
data class ElqStreamImpl<T>  private constructor(
        private val clazz: Class<T>,
        private val query: Query<T>
) : ElqStream<T> {

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

    override fun findAny(): T? {
        query.setOrder(null)
        return findFirst()
    }

    override fun findFirst(): T? {
        query.maxRows = 1;
        val resultList = query.findList()
        if (resultList.size == 0) {
            return null
        } else {
            return resultList[0]
        }
    }
}

fun <T> createElqStream(clazz: Class<T>): ElqStream<T> {
    return ElqStreamImpl(clazz)
}
