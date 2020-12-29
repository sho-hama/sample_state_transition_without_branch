package com.example.app.request


data class ArticleCreateRequest (
        val content : String,
        val publisherId : Int,
        val reviewerId : Int
)