package com.example.app.domain.entity

import com.example.app.request.ArticleCreateRequest
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*
import org.slf4j.LoggerFactory

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

    companion object {
        private var nextStatus: MutableMap<Pair<ArticleStatus, Event>, ArticleStatus> = mutableMapOf()
        private val log = LoggerFactory.getLogger(Article::class.java)
    }

    init {
        nextStatus[Pair(ArticleStatus.UNDER_EXAMINATION, Event.APPROVE)]   = ArticleStatus.APPROVED
        nextStatus[Pair(ArticleStatus.UNDER_EXAMINATION, Event.SEND_BACK)] = ArticleStatus.SEND_BACK
        nextStatus[Pair(ArticleStatus.APPROVED, Event.PUBLISH)]            = ArticleStatus.NOW_PUBLIC
        nextStatus[Pair(ArticleStatus.APPROVED, Event.WITHDRAW)]           = ArticleStatus.WITHDRAWAL
        nextStatus[Pair(ArticleStatus.SEND_BACK, Event.EXAMINE)]           = ArticleStatus.UNDER_EXAMINATION
        nextStatus[Pair(ArticleStatus.SEND_BACK, Event.WITHDRAW)]          = ArticleStatus.WITHDRAWAL
        nextStatus[Pair(ArticleStatus.NOW_PUBLIC, Event.WITHDRAW)]         = ArticleStatus.WITHDRAWAL
    }

    private fun getNextStatus(nowStatus: ArticleStatus, event: Event) : ArticleStatus? {
        return nextStatus[Pair(nowStatus, event)]
    }

    fun transitStatus(event : Event) : Article? {
        ArticleStatus.fromCodeToStatus(this.status)?.let { oldStatus ->
            this.status = getNextStatus(oldStatus, event)?.code ?: run {
                log.warn("Invalid event")
                return null
            }
            return this
        }
        log.warn("Invalid article status")
        return null
    }

    fun setPublishPeriod(publishStartTime : LocalDateTime, publishEndTime: LocalDateTime) {
        this.publishStartTime = publishStartTime
        this.publishEndTime = publishEndTime
    }
}


enum class Event {
    APPROVE,
    SEND_BACK,
    EXAMINE,
    PUBLISH,
    WITHDRAW
}

enum class ArticleStatus(val code: Byte) {
    UNDER_EXAMINATION(1),
    APPROVED(2),
    SEND_BACK(3),
    WITHDRAWAL(4),
    NOW_PUBLIC(5);
    companion object {
        fun fromCodeToStatus(statusCode : Byte) : ArticleStatus? {
            return values().firstOrNull { it.code == statusCode }
        }
    }
}
