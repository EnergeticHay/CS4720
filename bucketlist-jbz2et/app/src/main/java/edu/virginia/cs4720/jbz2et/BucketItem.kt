package edu.virginia.cs4720.jbz2et

import java.time.LocalDate
import java.util.UUID

data class BucketItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dueDate: LocalDate,
    val done: Boolean = false,
    val completedDate: LocalDate? = null
)