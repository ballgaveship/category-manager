package com.gaveship.category.domain.model

import com.gaveship.category.Mock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
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
class CategoryRepositoryCreateSpec(
    private val categoryRepository: CategoryRepository
) : ExpectSpec() {
    companion object {
        private const val ONE_TO_FIFTY_MESSAGE = "크기가 0에서 50 사이여야 합니다"
    }

    init {
        context("Category 생성을 할 때") {
            val targetCategory = Mock.category().single()
            expect("Name만 있으면 Category가 생성된다.") {
                val savedCategory = categoryRepository.save(targetCategory)

                savedCategory.id shouldNot beNull()
                savedCategory.name shouldBe targetCategory.name
            }

            expect("Name이 비어있어도 Category가 생성된다.") {
                targetCategory.name = ""
                val savedCategory = categoryRepository.save(targetCategory)

                savedCategory.id shouldNot beNull()
                savedCategory.name shouldBe targetCategory.name
            }

            expect("Name과 하위 Category 정보로 Category가 생성된다.") {
                val categoryChildren = Mock.category().chunked(10, 10).single()
                targetCategory.children = categoryChildren
                val savedCategory = categoryRepository.save(targetCategory)

                savedCategory.id shouldNot beNull()
                savedCategory.name shouldBe targetCategory.name
                savedCategory.children shouldNot beNull()
                savedCategory.children!! shouldHaveSize categoryChildren.size
            }

            expect("Name과 2Depth의 하위 Categories로 Category가 생성된다.") {
                val childrenOfChildrenOfChildren = Mock.category().chunked(10, 10).single()
                val childrenOfChildren = Mock.category(children = childrenOfChildrenOfChildren).chunked(10, 10).single()
                val categoryChildren = Mock.category(children = childrenOfChildren).chunked(20, 20).single()
                targetCategory.children = categoryChildren
                val savedCategory = categoryRepository.save(targetCategory)

                savedCategory.id shouldNot beNull()
                savedCategory.name shouldBe targetCategory.name
                savedCategory.children shouldNot beNull()
                savedCategory.children!! shouldHaveSize categoryChildren.size
                savedCategory.children!!.forEach { children ->
                    children.children shouldNot beNull()
                    children.children!! shouldHaveSize childrenOfChildren.size
                    children.children!!.forEach { childrenOfChildren ->
                        childrenOfChildren.children shouldNot beNull()
                        childrenOfChildren.children!! shouldHaveSize childrenOfChildrenOfChildren.size
                    }
                }
            }

            expect("Name이 임계치를 초과하면 Category를 생성할 수 없다.") {
                val nameThreshold = 51
                targetCategory.name = Arb.string(nameThreshold, nameThreshold).next()

                val constraintViolationException = shouldThrow<ConstraintViolationException> {
                    categoryRepository.save(targetCategory)
                }
                constraintViolationException.constraintViolations shouldHave
                        singleElement {
                            isEqualCheckOneToFiftyMessage(it) && isEqualTestName(it, targetCategory)
                        }
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