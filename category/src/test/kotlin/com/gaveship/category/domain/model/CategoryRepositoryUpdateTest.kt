package com.gaveship.category.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.TransactionSystemException
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@EnableJpaAuditing
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest(
    properties = [
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
    ]
)
class CategoryRepositoryUpdateTest(
    private val categoryRepository: CategoryRepository
): StringSpec() {
    init {
        "ID를 통한 Category 수정 성공 Test" {
            val targetName = "changed"
            val categoryChildren = Mock.category().chunked(10, 10).single().toSet()
            val targetCategory = Mock.category(children = categoryChildren).single()
            val savedCategory = categoryRepository.save(targetCategory)

            savedCategory.name = targetName
            val updatedCategory = categoryRepository.save(savedCategory)

            updatedCategory.name shouldBe targetName
        }

        "ID를 통한 Category 수정시 Name 글자 수 초과 실패 Test" {
            val targetName = Arb.string(100, 100).next()
            val targetCategory = Mock.category().single()
            val savedCategory = categoryRepository.save(targetCategory)

            savedCategory.name = targetName
            shouldThrow<TransactionSystemException> {
                categoryRepository.save(savedCategory)
            }
            categoryRepository.deleteById(savedCategory.id!!)
        }
    }
}