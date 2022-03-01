package com.gaveship.category.interfaces.web.v1

import com.gaveship.category.application.CategoryService
import com.gaveship.category.domain.model.Category
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    companion object {
        private const val GET_CATEGORY_URI = "/v1/categories/{id}"
    }

    @GetMapping
    fun getCategories(): List<Category> = categoryService.findAll()

    @GetMapping("/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Long
    ): Category = categoryService.find(categoryId)

    @PostMapping
    fun createCategory(
        @Valid @RequestBody(required = true) category: Category,
        uriComponentsBuilder: UriComponentsBuilder
    ): ResponseEntity<Void> {
        val createdCategory = categoryService.insert(
            category.apply {
                this.id = null
            }
        )
        val createCategoryId = createdCategory.id ?: throw IllegalArgumentException("ID can't not be null")
        val headers = HttpHeaders()
            .apply {
                this.location = uriComponentsBuilder
                    .createUri(GET_CATEGORY_URI, createCategoryId)
            }
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    private fun UriComponentsBuilder.createUri(
        path: String,
        vararg arguments: Any
    ): URI =
        this
            .path(path)
            .buildAndExpand(*arguments)
            .toUri()

    @PutMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: Long,
        @Valid @RequestBody(required = true) category: Category
    ): Category = categoryService.update(
        category.apply {
            this.id = categoryId
        }
    )

    @PatchMapping("/{categoryId}")
    fun patchCategory(
        @PathVariable categoryId: Long,
        @Valid @RequestBody(required = true) category: Category
    ): Category = categoryService.patch(
        category.apply {
            this.id = categoryId
        }
    )

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(
        @PathVariable categoryId: Long
    ): Unit = categoryService.delete(categoryId)
}