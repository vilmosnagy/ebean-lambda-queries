package com.github.vilmosnagy.elq.elqcore.model.statements.branch

import com.github.vilmosnagy.elq.elqcore.model.statements.Statement

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
internal data class CompareStatement(
        val v1: Statement,
        val v2: Statement,
        val compareType: CompareType
): Statement

