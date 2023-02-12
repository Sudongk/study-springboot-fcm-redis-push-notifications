package com.myboard.repository.articleComment;

import com.myboard.entity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    @Query("SELECT ac.id FROM ArticleComment ac WHERE ac.id = :articleCommentId AND ac.user.id = :userId")
    Optional<Long> findIdByUserIdAndArticleCommentId(Long articleCommentId, Long userId);

}
