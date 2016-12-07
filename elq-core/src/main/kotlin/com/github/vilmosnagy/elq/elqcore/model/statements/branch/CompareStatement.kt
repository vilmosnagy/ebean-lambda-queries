package com.github.vilmosnagy.elq.elqcore.model.statements.branch

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class CompareStatement(
        val v1: Statement,
        val v2: Statement,
        val compareType: CompareType
): Statement {

    /**
     * @return (true, 1)            if the comparsion is (stuff != null)
     * @return (true, 0)            if the comparsion is (stuff == null)
     * @return (false, anything)    if it's not a null check
     */
    internal fun isNullCheck(): Pair<Boolean, Int> {
        if (compareType == CompareType.NOT_EQUALS || compareType == CompareType.EQUALS) {
            if (v1.evaluate() == null && compareType == CompareType.EQUALS) return Pair(true, 0)
            if (v2.evaluate() == null && compareType == CompareType.EQUALS) return Pair(true, 0)

            if (v1.evaluate() == null && compareType == CompareType.NOT_EQUALS) return Pair(true, 1)
            if (v2.evaluate() == null && compareType == CompareType.NOT_EQUALS) return Pair(true, 1)
        }

        return Pair(false, Int.MIN_VALUE)
    }
}

