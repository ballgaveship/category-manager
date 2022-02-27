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
        log.info { "[category-service][insert][start]Category(name='${category.name}')" }
        if (category.id != null) throw IllegalArgumentException("ID can't not be exist")
        val insertedCategory = categoryRepository
            .save(category)
        log.debug { "[category-service][insert][inserted]$category" }
        return insertedCategory
    }

    @Transactional
    override fun update(category: Category): Category {
        log.info { "[category-service][update][start]Category(id='${category.id}', name='${category.name}')" }
        val categoryId = category.id ?: throw IllegalArgumentException("ID can't not be null")
        if (!categoryRepository.existsById(categoryId)) throw NotExistCategoryException("Not exist category (ID = $categoryId)")
        val updatedCategory = categoryRepository
            .save(category)
        log.debug { "[category-service][update][updated]$category" }
        return updatedCategory
    }

    @Transactional
    override fun delete(id: Long): Unit = categoryRepository
            .deleteById(id)

    @Transactional(readOnly = true)
    override fun findAll(): List<Category> {
        log.info { "[category-service][findAll][start]" }
        val foundCategories = categoryRepository
            .findAllByParentIdNull()
        log.debug { "[category-service][findAll]$foundCategories" }
        return foundCategories
    }

    @Transactional(readOnly = true)
    override fun find(id: Long): Category {
        log.info { "[category-service][find][start]id=$id" }
        val foundCategory = categoryRepository
            .findById(id)
            .orElseThrow {
                NotExistCategoryException(id)
            }
        log.debug { "[category-service][findAll]$foundCategory" }
        return foundCategory
    }
}