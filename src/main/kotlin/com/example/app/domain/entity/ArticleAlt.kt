package com.example.app.domain.entity

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "article-alt")
data class ArticleAlt (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id : Int? = null,

    @Column(name = "publish_start_time")
    var publishStartTime : LocalDateTime?,

    @Column(name = "publish_end_time")
    var publishEndTime : LocalDateTime?,

    @Column(name = "content")
    var content : String?,

    @Column(name = "status")
    var status : Byte,

    @Column(name = "publisherId")
    var publisherId : Int,

    @Column(name = "reviewerId")
    var reviewerId : Int
) : Serializable
