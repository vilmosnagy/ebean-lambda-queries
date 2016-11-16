package com.github.vilmosnagy.elq.elqcore.stream

import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
internal fun <E> Collection<E>.stream(): Stream<E> = StreamSupport.stream<E>(this.spliterator(), false)
internal fun <E> Collection<E>.spliterator(): Spliterator<E> = Spliterators.spliterator(this, 0)