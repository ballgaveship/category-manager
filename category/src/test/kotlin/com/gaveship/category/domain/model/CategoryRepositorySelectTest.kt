package com.gaveship.category.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.single
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@DataJpaTest(
    properties = [
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
    ]
)
class CategoryRepositorySelectTest(
    private val categoryRepository: CategoryRepository
): StringSpec() {
    init {
        "Root Categories 조회 성공 Test" {
            val targetCategory = Mock.category().single()
            val savedCategory = categoryRepository.save(targetCategory)

            val savedCategoryId = savedCategory.id ?: throw IllegalArgumentException()
            val foundCategories = categoryRepository.findAll()

            foundCategories shouldHaveSize 1
            foundCategories shouldHave singleElement { it.id == savedCategoryId }
        }

        "ID를 통한 Category 조회 성공 Test" {
            val categoryChildren = Mock.category().chunked(10, 10).single().toSet()
            val targetCategory = Mock.category(children = categoryChildren).single()
            val savedCategory = categoryRepository.save(targetCategory)

            val savedCategoryId = savedCategory.id ?: throw IllegalArgumentException()
            val foundCategory = categoryRepository.findById(savedCategoryId).get()

            foundCategory.name shouldBe savedCategory.name
            foundCategory.children shouldNot beNull()
            foundCategory.children!!.map { it.name } shouldBe
                    targetCategory.children!!.map { it.name }
        }

    }
}