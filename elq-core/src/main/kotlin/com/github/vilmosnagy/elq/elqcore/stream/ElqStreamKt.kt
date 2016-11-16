package com.github.vilmosnagy.elq.elqcore.stream

import com.avaje.ebean.Query

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
interface ElqStreamKt<T>: ElqStream<T> {
    val clazz: Class<T>
    val query: Query<T>

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