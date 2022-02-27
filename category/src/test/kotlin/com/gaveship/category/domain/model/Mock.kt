package com.gaveship.category.domain.model

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

object Mock {
    fun category(
        id: Long? = null,
        name: String? = null,
        children: Set<Category>? = null,
    ) = arbitrary {
        Category(
            id = id,
            name = name ?: Arb.string(0, 50).next(it),
            children = children
        )
    }
}