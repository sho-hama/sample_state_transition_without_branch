package com.example.app.service

import com.example.app.domain.entity.Article
import com.example.app.domain.entity.ArticleStatus.*
import com.example.app.domain.entity.ArticleStatus
import com.example.app.domain.repository.ArticleRepository
import com.example.app.request.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class ArticleService(
        private val articleRepository: ArticleRepository,
        private val articleStatusTransitions: ArticleStatusTransitions
) {
    companion object {
        private val log = LoggerFactory.getLogger(ArticleService::class.java)
    }

    fun getAllArticles() : List<Article>{
        return articleRepository.findAll()
    }

    fun createNewArticle(articleCreateRequest: ArticleCreateRequest) : Article {
        return articleRepository.save(Article(articleCreateRequest))
    }

    fun approveArticle(articleApproveRequest: ArticleApproveRequest) : Article? {
        articleRepository.findById(articleApproveRequest.articleId).let { article ->
            if(article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            articleStatusTransitions.receiveEvent(article.get(), Event.APPROVE)?.let { approvedArticle ->
                return articleRepository.save(approvedArticle)
            }
            return null
        }
    }

    fun sendBackArticle(articleSendBackRequest: ArticleSendBackRequest) : Article? {
        articleRepository.findById(articleSendBackRequest.articleId).let { article ->
            if(article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            articleStatusTransitions.receiveEvent(article.get(), Event.APPROVE)?.let { sentBackArticle ->
                return articleRepository.save(sentBackArticle)
            }
            return null
        }
    }

    fun publishArticle(articlePublishRequest: ArticlePublishRequest) : Article? {
        articleRepository.findById(articlePublishRequest.articleId).let { article ->
            if(article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            articleStatusTransitions.receiveEvent(article.get(), Event.PUBLISH)?.let { publishedArticle ->
                publishedArticle.publishStartTime = articlePublishRequest.publishStartDate
                publishedArticle.publishEndTime = articlePublishRequest.publishEndDate
                return articleRepository.save(publishedArticle)
            }
            return null
        }
    }

    fun withdrawArticle(articleWithdrawRequest: ArticleWithdrawRequest) : Article? {
        articleRepository.findById(articleWithdrawRequest.articleId).let { article ->
            if(article.isEmpty) {
                log.warn("Article not found")
                return null
            }
            articleStatusTransitions.receiveEvent(article.get(), Event.PUBLISH)?.let { withdrawalArticle ->
                return articleRepository.save(withdrawalArticle)
            }
            return null
        }
    }
}

@Component
class ArticleStatusTransitions {
    companion object {
        private var nextStatus: MutableMap<Pair<ArticleStatus, Event>, ArticleStatus> = mutableMapOf()
        private val log = LoggerFactory.getLogger(ArticleStatusTransitions::class.java)
    }

    init {
        nextStatus[Pair(UNDER_EXAMINATION, Event.APPROVE)] = APPROVED
        nextStatus[Pair(UNDER_EXAMINATION, Event.SEND_BACK)] = SEND_BACK
        nextStatus[Pair(APPROVED, Event.PUBLISH)] = NOW_PUBLIC
        nextStatus[Pair(APPROVED, Event.WITHDRAW)] = WITHDRAWAL
        nextStatus[Pair(SEND_BACK, Event.EXAMINE)] = UNDER_EXAMINATION
        nextStatus[Pair(SEND_BACK, Event.WITHDRAW)] = WITHDRAWAL
        nextStatus[Pair(NOW_PUBLIC, Event.WITHDRAW)] = WITHDRAWAL
    }


    private fun getNextStatus(nowStatus: ArticleStatus, event: Event) : ArticleStatus? {
        return nextStatus[Pair(nowStatus, event)]
    }

    fun receiveEvent(article : Article, event : Event) : Article? {
        ArticleStatus.fromCodeToStatus(article.status)?.let { oldStatus ->
            article.status = getNextStatus(oldStatus, event)?.code ?: run {
                log.warn("Invalid event")
                return null
            }
            return article
        }
        log.warn("Invalid article status")
        return null
    }
}

enum class Event {
    APPROVE,
    SEND_BACK,
    EXAMINE,
    PUBLISH,
    WITHDRAW
}
