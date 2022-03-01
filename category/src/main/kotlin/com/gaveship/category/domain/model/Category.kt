package com.gaveship.category.domain.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener::class)
class Category(
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @get:Size(min = 0, max = 50)
    @get:Column(nullable = false, length = 50)
    var name: String? = null,

    @get:JsonIgnore
    @get:Column(nullable = false, updatable = false)
    @get:CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.MIN,

    @get:JsonIgnore
    @get:LastModifiedDate
    var modifiedDate: LocalDateTime? = null,

    @get:ManyToOne
    @get:JoinColumn(name = "parentId")
    @get:JsonBackReference
    var parent: Category? = null,

    @get:Size(min = 0, max = 20)
    @get:OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @get:JoinColumn(name = "parentId")
    var children: List<Category>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Category(id=$id, name='$name', createdDate=$createdDate, modifiedDate=$modifiedDate, children=$children)"
    }
}