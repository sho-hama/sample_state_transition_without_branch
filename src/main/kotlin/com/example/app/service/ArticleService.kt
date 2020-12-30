package com.example.app.service

import com.example.app.domain.entity.Article
import com.example.app.domain.entity.Event
import com.example.app.domain.repository.ArticleRepository
import com.example.app.request.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ArticleService(
        private val articleRepository: ArticleRepository
) {
    companion object {
        private val log = LoggerFactory.getLogger(ArticleService::class.java)
    }

    fun getAllArticles() : List<Article> {
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
            article.get().transitStatus(Event.APPROVE)?.let { approvedArticle ->
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
            article.get().transitStatus(Event.APPROVE)?.let { sentBackArticle ->
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
            article.get().transitStatus(Event.PUBLISH)?.let { publishedArticle ->
                publishedArticle.setPublishPeriod(articlePublishRequest.publishStartDate, articlePublishRequest.publishEndDate)
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
            article.get().transitStatus(Event.WITHDRAW)?.let { withdrawalArticle ->
                return articleRepository.save(withdrawalArticle)
            }
            return null
        }
    }
}
