package com.gaveship.category.application

class NotExistCategoryException(override var message: String?) : RuntimeException(message) {
    constructor(id: Long): this(null) {
        message = "$id not found."
    }
}