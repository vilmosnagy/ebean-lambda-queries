package com.github.vilmosnagy.elq.elqcore.test.model

import java.util.*

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
data class TestEntity(
        var id: Int,
        var version: Date,
        var title: String,
        var valid: Boolean
)