package com.gaveship.category.application.impl

import com.gaveship.category.application.CategoryService
import com.gaveship.category.application.NotExistCategoryException
import com.gaveship.category.application.getByDepth
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
        log.info { "[category-service][update][start]Category(id='${category.id}', name='${category.name}', children='${category.children}')" }
        val categoryId = category.id ?: throw IllegalArgumentException("ID can't not be null")
        if (!categoryRepository.existsById(categoryId)) throw NotExistCategoryException("Not exist category (ID = $categoryId)")
        val savedCategory = categoryRepository
            .save(category)
        log.debug { "[category-service][update][updated]$savedCategory" }
        return savedCategory
    }

    @Transactional
    override fun patch(category: Category): Category {
        log.info { "[category-service][patch][start]Category(id='${category.id}', name='${category.name}', children='${category.children}')" }
        val categoryId = category.id ?: throw IllegalArgumentException("ID can't not be null")
        val foundCategory = find(categoryId)
        if (category.name != null) {
            foundCategory.name = category.name
        }
        if (category.children != null) {
            foundCategory.children = category.children
        }
        val savedCategory = categoryRepository
            .save(category)
        log.debug { "[category-service][patch][patched]$savedCategory" }
        return savedCategory
    }

    @Transactional
    override fun delete(id: Long) {
        log.info { "[category-service][delete][start]Category(id='$id')" }
        categoryRepository
            .deleteById(id)
        log.debug { "[category-service][delete][deleted]" }
    }

    @Transactional(readOnly = true)
    override fun findAll(depth: Int): List<Category> {
        log.info { "[category-service][findAll][start]" }
        val foundCategories = categoryRepository
            .findAllByParentIdNull()
        log.debug { "[category-service][findAll]$foundCategories" }
        return foundCategories.map { it.getByDepth(depth) }
    }

    @Transactional(readOnly = true)
    override fun find(id: Long, depth: Int): Category {
        log.info { "[category-service][find][start]id=$id" }
        val foundCategory = categoryRepository
            .findById(id)
            .run {
                orElseThrow { NotExistCategoryException(id) }
            }
        log.debug { "[category-service][find]$foundCategory" }
        return foundCategory.getByDepth(depth)
    }
}
