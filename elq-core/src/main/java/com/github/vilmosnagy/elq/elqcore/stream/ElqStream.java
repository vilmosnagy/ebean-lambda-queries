package com.github.vilmosnagy.elq.elqcore.stream;

import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collector;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
public interface ElqStream<T> {

    public T findAny();

    public T findFirst();

    public Long count();

    public ElqStream<T> filter(@NotNull Predicate<T> predicate);

    public <R, A> R collect(@NotNull Collector<? super T, A, R> collector);
}
