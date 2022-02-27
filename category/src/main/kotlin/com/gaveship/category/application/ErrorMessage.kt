package com.gaveship.category.application

import java.time.Instant

data class ErrorMessage(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val path: String,
)