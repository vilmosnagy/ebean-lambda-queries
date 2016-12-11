package com.github.vilmosnagy.elq.elqcore.service

import com.github.vilmosnagy.elq.elqcore.model.lqexpressions.field.FieldReference
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
@Singleton
internal class JavaPropertyService
@Inject constructor() {

    fun parsePropertyChainToGetters(fieldReference: FieldReference): List<Method> {
        val clazz = fieldReference.parentClazz
        val fieldName = fieldReference.fieldName
        val getterMethodName = "get${capitalizeFirstLetter(fieldName)}"
        return listOf(clazz.getDeclaredMethod(getterMethodName))
    }

    private fun capitalizeFirstLetter(string: String): String {
        return string.mapIndexed { i, c -> if(i == 0) c.toUpperCase() else c }.joinToString(separator = "")
    }

}