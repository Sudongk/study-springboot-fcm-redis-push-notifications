package com.myboard.repository.article;

import com.myboard.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryEx {

    @Query("SELECT a.id FROM Article a WHERE a.id = :articleId AND a.user.id = :userId")
    Optional<Long> findIdByUserIdAndArticleId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :articleId")
    void increaseViewCount(@Param("articleId") Long articleId);

}

