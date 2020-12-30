package com.example.app.domain.repository

import com.example.app.domain.entity.ArticleAlt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepositoryAlt: JpaRepository<ArticleAlt, Int>
