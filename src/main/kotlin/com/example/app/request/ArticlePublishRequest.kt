package com.example.app.request

import java.time.LocalDateTime

data class ArticlePublishRequest(
        val articleId : Int,
        val publishStartDate : LocalDateTime,
        val publishEndDate : LocalDateTime
)
