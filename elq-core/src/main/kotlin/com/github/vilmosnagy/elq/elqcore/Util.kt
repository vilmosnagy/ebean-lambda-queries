package com.github.vilmosnagy.elq.elqcore

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
internal data class IncludedArgs(val args: List<Any>)

internal fun includedArgs(vararg args: Any) = IncludedArgs(args.toList())

abstract class EqualsAndHashCode {
    internal abstract val included : IncludedArgs

    override fun equals(other: Any?) = when {
        this === other -> true
        other is EqualsAndHashCode -> included == other.included
        else -> false
    }

    override fun hashCode() = included.hashCode()

    override fun toString() = included.toString()
}

internal fun String.subStringBetween(after: Char, before: Char): String {
    return substringAfter(after).substringBefore(before)
}

internal fun <E> Deque<E>.pop(methodCount: Int): List<E> {
    val retList = mutableListOf<E>()
    for (i in 0..(methodCount - 1)) {
        retList += pop()
    }
    return retList.reversed()
}

internal val Method.isStatic: Boolean
    get() = Modifier.isStatic(modifiers)