package com.github.vilmosnagy.elq.elqcore.model;

import kotlin.jvm.functions.Function1;

import java.util.function.Function;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
public class FunctionIntegerIsFive implements Function1<Integer, Boolean> {

    @Override
    public Boolean invoke(Integer integer) {
        return integer == 5;
    }

}
