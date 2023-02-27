package com.myboard.repository.articleComment;

import com.myboard.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    @Query("SELECT ac FROM ArticleComment ac WHERE ac.article.id = :articleId")
    List<ArticleComment> findByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT ac FROM ArticleComment ac WHERE ac.id = :articleCommentId AND ac.user.id = :userId")
    Optional<ArticleComment> findByArticleCommentIdAndUserId(Long articleCommentId, Long userId);

}
