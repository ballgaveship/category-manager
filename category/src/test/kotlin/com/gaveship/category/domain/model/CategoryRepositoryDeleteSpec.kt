package com.gaveship.category.domain.model

import com.gaveship.category.Mock
import com.gaveship.category.application.NotExistCategoryException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldNot
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.single
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@EnableJpaAuditing
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest(
    showSql = true,
    properties = [
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
    ]
)
class CategoryRepositoryDeleteSpec(
    private val categoryRepository: CategoryRepository
) : ExpectSpec() {
    init {
        context("Category 삭제를 할 때") {
            val targetCategory = Mock.category().single()
            expect("ID만 있으면 Category가 삭제된다.") {
                val savedCategory = categoryRepository.save(targetCategory)
                val savedCategoryId = savedCategory.id ?: throw IllegalArgumentException()
                categoryRepository.deleteById(savedCategoryId)

                shouldThrow<NotExistCategoryException> {
                    categoryRepository.findById(savedCategoryId).orElseThrow {
                        throw NotExistCategoryException(savedCategoryId)
                    }
                }
            }

            expect("ID만 있으면 하위 Cateogry 정보를 포함한 Category가 삭제된다.") {
                val categoryChildren = Mock.category().chunked(10, 10).single()
                targetCategory.children = categoryChildren
                val savedCategory = categoryRepository.save(targetCategory)

                val savedCategoryId = savedCategory.id ?: throw IllegalArgumentException()
                categoryRepository.deleteById(savedCategoryId)

                shouldThrow<NotExistCategoryException> {
                    categoryRepository.findById(savedCategoryId).orElseThrow {
                        throw NotExistCategoryException(savedCategoryId)
                    }
                }
                savedCategory.children!! shouldNot beNull()
                savedCategory.children!!.forEach {
                    shouldThrow<NotExistCategoryException> {
                        val categoryId = it.id ?: throw IllegalArgumentException()
                        categoryRepository.findById(categoryId).orElseThrow {
                            throw NotExistCategoryException(categoryId)
                        }
                    }
                }
            }
        }
    }
}