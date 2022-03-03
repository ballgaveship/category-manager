package com.gaveship.category.interfaces.web.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.gaveship.category.Mock
import com.gaveship.category.application.CategoryService
import com.gaveship.category.application.NotExistCategoryException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.single
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class CategoryControllerIntegrationTest(
    private val objectMapper: ObjectMapper,
    ctx: WebApplicationContext
) : StringSpec() {
    @MockkBean
    private lateinit var categoryService: CategoryService

    private val mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build()

    companion object {
        private const val BASE_ENDPOINT = "/v1/categories"
    }

    init {
        "전체 Category 조회 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val targetCategory = Mock.category(children = categoryChildren).single()
            val response = listOf(targetCategory)

            every { categoryService.findAll() } returns response

            mockMvc.perform(get(BASE_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
        }

        "Category 등록 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val createdCategoryId = 1L
            val targetCategory = Mock.category(children = categoryChildren).single()
            val createdCategory = Mock.category(id = createdCategoryId, children = categoryChildren).single()
            every { categoryService.insert(targetCategory) } returns createdCategory

            mockMvc.perform(post(BASE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(targetCategory))
            )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/v1/categories/$createdCategoryId"))
                .andExpect(jsonPath("$").doesNotExist())
        }

        "Category 조회 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val targetCategory = Mock.category(id = 1, children = categoryChildren).single()
            val targetCategoryId = targetCategory.id ?: throw IllegalArgumentException()
            every { categoryService.find(targetCategoryId) } returns targetCategory

            mockMvc.perform(get("$BASE_ENDPOINT/$targetCategoryId"))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(objectMapper.writeValueAsString(targetCategory)))
        }

        "Category 조회 NotExistCategory 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val targetCategory = Mock.category(id = 1, children = categoryChildren).single()
            val targetCategoryId = targetCategory.id ?: throw IllegalArgumentException()
            every { categoryService.find(targetCategoryId) } throws NotExistCategoryException(targetCategoryId)

            mockMvc.perform(get("$BASE_ENDPOINT/$targetCategoryId"))
                .andDo(print())
                .andExpect(status().isBadRequest)
        }

        "Category 조회 InternalServerError 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val targetCategory = Mock.category(id = 1, children = categoryChildren).single()
            val targetCategoryId = targetCategory.id ?: throw IllegalArgumentException()
            every { categoryService.find(targetCategoryId) } throws RuntimeException()

            mockMvc.perform(get("$BASE_ENDPOINT/$targetCategoryId"))
                .andDo(print())
                .andExpect(status().is5xxServerError)
        }

        "Category 수정 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val originCategory = Mock.category(id = 1, name = "origin", children = categoryChildren).single()
            val targetCategory = Mock.category(name = "target").single()
            val originCategoryId = originCategory.id ?: throw IllegalArgumentException()
            every { categoryService.update(originCategory) } returns targetCategory

            mockMvc.perform(put("$BASE_ENDPOINT/$originCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(targetCategory))
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(objectMapper.writeValueAsString(targetCategory)))
        }

        "Category 삭제 테스트" {
            val targetCategoryId = 1L
            every { categoryService.delete(targetCategoryId) } just runs

            mockMvc.perform(delete("$BASE_ENDPOINT/$targetCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isNoContent)
                .andExpect(jsonPath("$").doesNotExist())
        }

        "Category 부분 수정 테스트" {
            val categoryChildren = Mock.category().chunked(10, 10).single()
            val originCategory = Mock.category(id = 1, name = "origin", children = categoryChildren).single()
            val targetCategory = Mock.category(name = "test").single()
            val patchedCategory = Mock.category(name = "test", children = categoryChildren).single()
            val originCategoryId = originCategory.id ?: throw IllegalArgumentException()

            every { categoryService.patch(originCategory) } returns patchedCategory

            mockMvc.perform(patch("$BASE_ENDPOINT/$originCategoryId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(targetCategory))
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(objectMapper.writeValueAsString(patchedCategory)))
        }
    }
}