package com.github.vilmosnagy.elq.elqcore.model.statements.branch

internal enum class CompareType {
    NOT_EQUALS { override fun negate() = EQUALS },
    EQUALS { override fun negate() = NOT_EQUALS },

    GREATER_THAN_OR_EQUALS { override fun negate() = LESS_THAN },
    LESS_THAN { override fun negate() = GREATER_THAN_OR_EQUALS },
    LESS_THAN_OR_EQUALS { override fun negate() = GREATER_THAN },
    GREATER_THAN { override fun negate() = LESS_THAN_OR_EQUALS };

    internal abstract fun negate(): CompareType
}