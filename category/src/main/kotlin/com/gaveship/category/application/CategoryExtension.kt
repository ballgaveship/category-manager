package com.gaveship.category.application

import com.gaveship.category.domain.model.Category

object CategoryExtension {
    fun checkDepth(depth: Int, targetDepth: Int): Boolean {
        return depth < targetDepth || targetDepth < 0
    }
}

fun Category.getByDepth(targetDepth: Int, currentDepth: Int = 0): Category {
    var depth = currentDepth
    val children = this.children
    return Category(
        id = this.id,
        name = this.name,
        createdDate = this.createdDate,
        modifiedDate = this.modifiedDate,
        children = if (children != null && children.isNotEmpty() && CategoryExtension.checkDepth(depth, targetDepth)) {
            depth += 1
            children.map {
                Category(
                    id = it.id,
                    name = it.name,
                    createdDate = it.createdDate,
                    modifiedDate = it.modifiedDate,
                    children = if (CategoryExtension.checkDepth(depth, targetDepth)) {
                        depth += 1
                        val categories = it.children?.map { childrenCategory ->
                            childrenCategory.getByDepth(targetDepth, depth)
                        }
                        depth -= 1
                        categories
                    } else {
                        listOf()
                    }
                )
            }
        } else {
            listOf()
        }
    )
}