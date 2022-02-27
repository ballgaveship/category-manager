package com.gaveship.category.application.impl

import com.gaveship.category.application.CategoryService
import com.gaveship.category.application.NotExistCategoryException
import com.gaveship.category.domain.model.Category
import com.gaveship.category.domain.model.CategoryRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @Transactional
    override fun insert(category: Category): Category {
        log.info { "[category-service][insert]Category(name='${category.name}')" }
        if (category.id != null) throw IllegalArgumentException("ID can't not be exist")
        val createdCategory = categoryRepository
            .save(category)
        log.debug { "[category-service][insert][created]$category" }
        return createdCategory
    }

    @Transactional
    override fun update(category: Category): Category {
        val categoryId = category.id ?: throw IllegalArgumentException("ID can't not be null")
        if (!categoryRepository.existsById(categoryId)) throw NotExistCategoryException("Not exist category (ID = $categoryId)")
        return categoryRepository
            .save(category)
    }

    @Transactional
    override fun delete(id: Long): Unit = categoryRepository
            .deleteById(id)

    @Transactional(readOnly = true)
    override fun findAll(): List<Category> = categoryRepository
            .findAllByParentIdNull()

    @Transactional(readOnly = true)
    override fun find(id: Long): Category = categoryRepository
            .findById(id)
            .orElseThrow {
                NotExistCategoryException(id)
            }
}