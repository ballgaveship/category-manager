package com.gaveship.category.application

import com.gaveship.category.domain.model.Category

interface CategoryService {
    fun insert(category: Category): Category
    fun update(category: Category): Category
    fun patch(category: Category): Category
    fun delete(id: Long)
    fun findAll(): List<Category>
    fun find(id: Long): Category
}