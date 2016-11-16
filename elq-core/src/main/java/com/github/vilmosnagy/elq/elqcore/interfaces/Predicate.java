package com.github.vilmosnagy.elq.elqcore.interfaces;

import java.io.Serializable;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
public interface Predicate<T> extends Serializable {

    Boolean test(T t);

}
