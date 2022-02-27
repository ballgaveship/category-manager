package com.gaveship.category.domain.model

import com.gaveship.category.Mock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import io.kotest.property.Arb
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@EnableJpaAuditing
@DataJpaTest(
    showSql = true,
    properties = [
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
    ]
)
class CategoryRepositoryCreateTest(
    private val categoryRepository: CategoryRepository
): StringSpec() {
    companion object {
        private const val ONE_TO_FIFTY_MESSAGE = "크기가 0에서 50 사이여야 합니다"
    }

    init {
        "Category 단일 저장 성공 Test" {
            val targetCategory = Mock.category().single()

            val savedCategory = categoryRepository.save(targetCategory)

            savedCategory.id shouldNot beNull()
            savedCategory.name shouldBe targetCategory.name
        }

        "Category 하위 계층 저장 성공 Test" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val targetCategory = Mock.category(children = categoryChildren).single()

            val savedCategory = categoryRepository.save(targetCategory)

            savedCategory.id shouldNot beNull()
            savedCategory.name shouldBe targetCategory.name
            savedCategory.children shouldNot beNull()
            savedCategory.children!! shouldHaveSize categoryChildren.size
        }

        "Category 저장시 Name 이 Empty String 인 경우 성공 Test" {
            val emptyString = ""
            val targetCategory = Mock.category(name = emptyString).single()

            val savedCategory = categoryRepository.save(targetCategory)

            savedCategory.id shouldNot beNull()
            savedCategory.name shouldBe targetCategory.name
        }

        "Category 저장시 Name 글자 수 초과 실패 Test" {
            val targetSize = 51
            val targetCategory = Mock.category(name = Arb.string(targetSize, targetSize).next()).single()

            val constraintViolationException = shouldThrow<ConstraintViolationException> {
                categoryRepository.save(targetCategory)
            }
            constraintViolationException.constraintViolations shouldHave
                    singleElement {
                        isEqualCheckOneToFiftyMessage(it) && isEqualTestName(it, targetCategory)
                    }
        }
    }

    private fun isEqualCheckOneToFiftyMessage(it: ConstraintViolation<*>) =
        it.message == ONE_TO_FIFTY_MESSAGE

    private fun isEqualTestName(
        it: ConstraintViolation<*>,
        targetCategory: Category
    ) = it.invalidValue.toString() == targetCategory.name
}