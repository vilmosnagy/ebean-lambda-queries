package com.github.vilmosnagy.elq.elqcore.model.statements.branch

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement

/**
 * @author Vilmos Nagy <vilmos.nagy@outlook.com>
 */
data class BranchedStatement(
        val compareStatement: CompareStatement,
        val branch01: Statement,
        val branch02: Statement
): Statement