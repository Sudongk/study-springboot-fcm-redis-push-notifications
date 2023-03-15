package com.myboard.repository.article;

import com.myboard.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryEx {

    @Query("SELECT a FROM Article a WHERE a.user.id in (:userIds)")
    List<Article> findAllByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT a FROM Article a WHERE a.user.id in (:boardIds)")
    List<Article> findAllByBoardIds(@Param("boardIds") List<Long> boardIds);

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId")
    List<Article> findAllByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT a FROM Article a WHERE a.id = :articleId AND a.user.id = :userId")
    Optional<Article> findByUserIdAndArticleId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :articleId")
    void increaseViewCount(@Param("articleId") Long articleId);

    @Query("SELECT a FROM Article a JOIN FETCH a.user WHERE a.id = :articleId")
    Optional<Article> findArticleByIdFetchJoin(@Param("articleId") Long articleId);
}

