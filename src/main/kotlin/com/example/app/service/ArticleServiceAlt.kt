package com.example.app.service

import com.example.app.domain.entity.ArticleAlt
import com.example.app.domain.entity.ArticleStatus
import com.example.app.domain.repository.ArticleRepositoryAlt
import com.example.app.request.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ArticleServiceAlt(
        private val articleRepositoryAlt: ArticleRepositoryAlt
) {
    companion object {
        private val log = LoggerFactory.getLogger(ArticleService::class.java)
    }

    fun getAllArticles(): List<ArticleAlt> {
        return articleRepositoryAlt.findAll()
    }

    fun createNewArticle(articleCreateRequest: ArticleCreateRequest): ArticleAlt {
        return articleRepositoryAlt.save(
                ArticleAlt(
                        publishStartTime = null,
                        publishEndTime = null,
                        content = articleCreateRequest.content,
                        status = ArticleStatus.UNDER_EXAMINATION.code,
                        publisherId = articleCreateRequest.publisherId,
                        reviewerId = articleCreateRequest.reviewerId
                )
        )
    }

    fun approveArticle(articleApproveRequest: ArticleApproveRequest): ArticleAlt? {
        articleRepositoryAlt.findById(articleApproveRequest.articleId).let { article ->
            if (article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            article.get().let {
                if (it.status == ArticleStatus.UNDER_EXAMINATION.code) {
                    it.status = ArticleStatus.APPROVED.code
                    return it
                }
                log.warn("Invalid article status")
                return null
            }
        }
    }

    fun sendBackArticle(articleSendBackRequest: ArticleSendBackRequest): ArticleAlt? {
        articleRepositoryAlt.findById(articleSendBackRequest.articleId).let { article ->
            if (article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            article.get().let {
                if (it.status == ArticleStatus.UNDER_EXAMINATION.code) {
                    it.status = ArticleStatus.SEND_BACK.code
                    return it
                }
                log.warn("Invalid article status")
                return null
            }
        }
    }

    fun publishArticle(articlePublishRequest: ArticlePublishRequest): ArticleAlt? {
        articleRepositoryAlt.findById(articlePublishRequest.articleId).let { article ->
            if (article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            article.get().let {
                if (it.status == ArticleStatus.APPROVED.code) {
                    it.status = ArticleStatus.NOW_PUBLIC.code
                    it.publishStartTime = articlePublishRequest.publishStartDate
                    it.publishEndTime = articlePublishRequest.publishEndDate
                    return it
                }
                log.warn("Invalid article status")
                return null
            }
        }
    }

    fun withdrawArticle(articleWithdrawRequest: ArticleWithdrawRequest): ArticleAlt? {
        articleRepositoryAlt.findById(articleWithdrawRequest.articleId).let { article ->
            if (article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            article.get().let {
                if (it.status == ArticleStatus.APPROVED.code ||
                        it.status == ArticleStatus.SEND_BACK.code ||
                        it.status == ArticleStatus.NOW_PUBLIC.code
                ) {
                    it.status = ArticleStatus.WITHDRAWAL.code
                    return it
                }
                log.warn("Invalid article status")
                return null
            }
        }
    }
}
