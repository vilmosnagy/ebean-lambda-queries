package com.github.vilmosnagy.elq.elqcore.model.statements.branch

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement
import com.github.vilmosnagy.elq.elqcore.model.statements.kotlin.ThrowUninitializedPropertyAccessException

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class BranchedStatement(
        internal val compareStatement: CompareStatement,
        internal val branch01: Statement,
        internal val branch02: Statement
): Statement {

    fun evaluateIfStraightForward(): Statement {
        val (nullCheck, branchIndex) = compareStatement.isNullCheck()
        if (nullCheck) {
            val exceptionBranch = if (branchIndex == 0) branch02 else branch01
            val valueBranch = if (branchIndex == 0) branch01 else branch02
            if (exceptionBranch.evaluate() == ThrowUninitializedPropertyAccessException) {
                return valueBranch
            }
        }

        return this
    }
}