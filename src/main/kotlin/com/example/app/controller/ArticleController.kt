package com.example.app.controller

import com.example.app.request.*
import com.example.app.service.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article")
class ArticleController(private val articleService: ArticleService) {


    @GetMapping("/")
    fun getAllArticles(): ResponseEntity<Any> {
        try {
            return ResponseEntity.ok(articleService.getAllArticles())
        } catch (e: Exception){
            throw e
        }
    }

    @PostMapping("/create")
    fun createArticle(
            @RequestBody articleCreateRequest: ArticleCreateRequest
    ): ResponseEntity<Any> {
        try {
            return ResponseEntity.ok(articleService.createNewArticle(articleCreateRequest))
        } catch (e: Exception){
            throw e
        }
    }

    @PostMapping("/approve")
    fun requestApproveArticle(
            @RequestBody articleApproveRequest: ArticleApproveRequest
    ): ResponseEntity<Any> {
        try {
            articleService.approveArticle(articleApproveRequest)?.let {
                return ResponseEntity.ok(it)
            }
            return ResponseEntity.notFound().build()
        } catch (e: Exception){
            throw e
        }
    }

    @PostMapping("/send-back")
    fun sendBackArticle(
            @RequestBody articleSendBackRequest: ArticleSendBackRequest
    ): ResponseEntity<Any> {
        try {
            articleService.sendBackArticle(articleSendBackRequest)?.let {
                return ResponseEntity.ok(it)
            }
            return ResponseEntity.notFound().build()
        } catch (e: Exception){
            throw e
        }
    }

    @PostMapping("/publish")
    fun requestNewArticle(
            @RequestBody articlePublishRequest: ArticlePublishRequest
    ): ResponseEntity<Any> {
        try {
            articleService.publishArticle(articlePublishRequest)?.let {
                return ResponseEntity.ok(it)
            }
            return ResponseEntity.notFound().build()
        } catch (e: Exception){
            throw e
        }
    }

    @PostMapping("/withdraw")
    fun requestWithdrawArticle(
            @RequestBody articleWithdrawRequest: ArticleWithdrawRequest
    ): ResponseEntity<Any> {
        try {
            articleService.withdrawArticle(articleWithdrawRequest)?.let {
                return ResponseEntity.ok(it)
            }
            return ResponseEntity.notFound().build()
        } catch (e: Exception){
            throw e
        }
    }
}
