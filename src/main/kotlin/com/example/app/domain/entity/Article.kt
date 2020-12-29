package com.example.app.domain.entity


import com.example.app.request.ArticleCreateRequest
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "article")
data class Article (
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
) : Serializable {

    constructor(articleCreateRequest: ArticleCreateRequest) : this (
            publishStartTime = null,
            publishEndTime = null,
            content = articleCreateRequest.content,
            status = ArticleStatus.UNDER_EXAMINATION.code,
            publisherId = articleCreateRequest.publisherId,
            reviewerId = articleCreateRequest.reviewerId
    )
}

enum class ArticleStatus(val code: Byte) {
    UNDER_EXAMINATION(1),
    APPROVED(2),
    SEND_BACK(3),
    WITHDRAWAL(4),
    NOW_PUBLIC(5);

    companion object {
        fun fromCodeToStatus(statusCode : Byte) : ArticleStatus? {
            return values().firstOrNull {it.code == statusCode}
        }
    }
}