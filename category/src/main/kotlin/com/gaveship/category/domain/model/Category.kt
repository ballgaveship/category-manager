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

    @Size(min = 0, max = 50)
    var name: String = "",

    @get:JsonIgnore
    @get:Column(updatable = false)
    @get:CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.MIN,

    @get:JsonIgnore
    @get:Column(insertable = false)
    @get:LastModifiedDate
    var modifiedDate: LocalDateTime? = null,

    @get:ManyToOne
    @get:JoinColumn(name = "parentId")
    @get:JsonBackReference
    var parent: Category? = null,

    @get:OneToMany(cascade = [CascadeType.ALL])
//    @get:OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @get:JoinColumn(name = "parentId")
    var children: List<Category>? = null
)