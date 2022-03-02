package com.gaveship.category

import com.gaveship.category.domain.model.Category
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import java.time.LocalDateTime

object Mock {
    fun category(
        id: Long? = null,
        name: String? = null,
        children: List<Category>? = null,
    ) = arbitrary {
        Category(
            id = id,
            name = name ?: Arb.string(0, 50).next(it),
            createdDate = LocalDateTime.now(),
            modifiedDate = LocalDateTime.now(),
            children = children
        )
    }
}