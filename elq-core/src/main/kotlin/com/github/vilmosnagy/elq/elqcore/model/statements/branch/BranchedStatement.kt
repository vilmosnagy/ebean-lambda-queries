package com.github.vilmosnagy.elq.elqcore.model.statements.branch

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class BranchedStatement(
        internal val compareStatement: CompareStatement,
        internal val branch01: Statement,
        internal val branch02: Statement
): Statement