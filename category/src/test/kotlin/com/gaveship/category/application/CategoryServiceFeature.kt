package com.gaveship.category.application

import com.gaveship.category.Mock
import com.gaveship.category.domain.model.Category
import com.gaveship.category.domain.model.CategoryRepository
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.single
import io.mockk.every
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class CategoryServiceFeature(
    val categoryService: CategoryService
) : FeatureSpec() {
    @MockkBean
    private lateinit var categoryRepository: CategoryRepository
    private val complexCategory = createComplexCategory()
    private val complexCategories =
        listOf(
            createComplexCategory(),
            createComplexCategory(),
            createComplexCategory(),
            createComplexCategory()
        )

    init {
        beforeTest {
            val mockCategoryId = 1L
            every { categoryRepository.findById(mockCategoryId) } returns Optional.of(complexCategory)
            every { categoryRepository.findAllByParentIdNull() } returns complexCategories
        }

        feature("상위 카테고리를 이용해") {
            val topCategoryId = 1L
            scenario("해당 카테고리의 하위의 모든 카테고리를 리턴 한다.") {
                val foundCategory = categoryService.find(topCategoryId)
                foundCategory shouldBe complexCategory
            }

            scenario("지정된 Depth 만큼의 카테고리를 포함한 해당 카테고리의 하위의 모든 카테고리를 리턴 한다.") {
                val depth = 3
                val foundCategory = categoryService.find(topCategoryId, depth)
                val complexCategoryByDepth = complexCategory.getByDepth(depth)
                foundCategory shouldBe complexCategoryByDepth
            }
        }

        feature("상위 카테고리를 지정하지 않을 시") {
            scenario("전체 카테고리를 리턴한다.") {
                val foundCategories = categoryService.findAll()
                foundCategories shouldBe complexCategories
            }

            scenario("지정된 Depth 만큼의 카테고리를 포함한 전체 카테고리를 리턴한다.") {
                val depth = 3
                val foundCategories = categoryService.findAll()
                val complexCategoriesByDepth = complexCategories.map { it.getByDepth(depth) }
                foundCategories shouldBe complexCategoriesByDepth
            }
        }
    }

    private fun createComplexCategory(): Category {
        val childrenOfChildrenOfChildren = Mock.category(id = 3).chunked(10, 10).single()
        val childrenOfChildren = Mock.category(id = 2, children = childrenOfChildrenOfChildren).chunked(10, 10).single()
        val categoryChildren = Mock.category(id = 1, children = childrenOfChildren).chunked(20, 20).single()
        return Mock.category(id = 0, children = categoryChildren).single()
    }
}