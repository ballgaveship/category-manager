package com.gaveship.category.domain.model

import com.gaveship.category.Mock
import com.gaveship.category.application.NotExistCategoryException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
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
class CategoryRepositoryDeleteTest(
    private val categoryRepository: CategoryRepository
): StringSpec() {
    init {
        "ID를 통한 단일 Category 삭제 성공 Test" {
            val targetCategory = Mock.category().single()
            val savedCategory = categoryRepository.save(targetCategory)

            val savedCategoryId = savedCategory.id ?: throw IllegalArgumentException()
            categoryRepository.deleteById(savedCategoryId)

            shouldThrow<NotExistCategoryException> {
                categoryRepository.findById(savedCategoryId).orElseThrow {
                    throw NotExistCategoryException(savedCategoryId)
                }
            }
        }

        "ID를 통한 하위 계층을 가진 Category 삭제 성공 Test" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val targetCategory = Mock.category(children = categoryChildren).single()
            val savedCategory = categoryRepository.save(targetCategory)

            val savedCategoryId = savedCategory.id ?: throw IllegalArgumentException()
            categoryRepository.deleteById(savedCategoryId)

            shouldThrow<NotExistCategoryException> {
                categoryRepository.findById(savedCategoryId).orElseThrow {
                    throw NotExistCategoryException(savedCategoryId)
                }
            }
        }
    }
}