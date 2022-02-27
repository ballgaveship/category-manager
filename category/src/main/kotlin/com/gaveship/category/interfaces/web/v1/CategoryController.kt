package com.gaveship.category.interfaces.web.v1

import com.gaveship.category.application.CategoryService
import com.gaveship.category.domain.model.Category
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    @GetMapping
    fun getCategories(): List<Category> = categoryService.findAll()

    @GetMapping("/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Long
    ): Category = categoryService.find(categoryId)

    @PostMapping
    fun createCategory(
        @Valid @RequestBody(required = true) category: Category
    ): Category = categoryService.insert(
        category.apply {
            this.id = null
        }
    )

    @PutMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable categoryId: Long,
        @Valid @RequestBody(required = true) category: Category
    ): Category = categoryService.update(
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