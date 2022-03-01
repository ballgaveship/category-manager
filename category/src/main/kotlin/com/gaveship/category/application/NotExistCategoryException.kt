package com.gaveship.category.application

class NotExistCategoryException(override var message: String?) : RuntimeException(message) {
    constructor(id: Long): this(null) {
        message = "Category not exist. id = $id"
    }
}