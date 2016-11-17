package com.github.vilmosnagy.elq.elqcore.interfaces

/**
 * An Expression Builder.
 *
 * Should be able to build standard expressions from parsed [Predicate] instances.
 *
 * Most of the methods represents an expression, gets a fieldName and a constant value.
 * These methods should return the built expression between the field represented by the
 * `fieldName` parameter, and the constant represented by the `value` parameter.
 *
 * @param T base type of all expressions
 *
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
public interface ExpressionBuilder<T> {

    /**
     * Should return an expression, representing the following operation: `entity.fieldName == value`
     *
     * @param fieldName the name of the entity's field
     * @param value constant value on the right hand side of the expression
     */
    fun equals(fieldName: String, value: Any?): T

    /**
     * Should return an expression, representing the following operation: `entity.fieldName < value`
     *
     * @param fieldName the name of the entity's field
     * @param value constant value on the right hand side of the expression
     */
    fun lessThan(fieldName: String, value: Any?): T

    /**
     * Should return an expression, representing the following operation: `entity.fieldName >= value`
     *
     * @param fieldName the name of the entity's field
     * @param value constant value on the right hand side of the expression
     */
    fun greaterThanOrEquals(fieldName: String, value: Any?): T

    /**
     * Should return an expression, representing the following operation: `entity.fieldName <= value`
     *
     * @param fieldName the name of the entity's field
     * @param value constant value on the right hand side of the expression
     */
    fun lessThanOrEquals(fieldName: String, value: Any?): T

    /**
     * Should return an expression, representing the following operation: `entity.fieldName > value`
     *
     * @param fieldName the name of the entity's field
     * @param value constant value on the right hand side of the expression
     */
    fun greaterThan(fieldName: String, value: Any?): T

}