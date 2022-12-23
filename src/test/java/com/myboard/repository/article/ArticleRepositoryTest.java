package com.myboard.repository.article;

import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.articleComment.ArticleCommentResponseDto;
import com.myboard.repository.article.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void articleDetail() {
        ArticleResponseDto articleResponseDto = articleRepository.articleDetail(1L);
        if (articleResponseDto != null) {
            System.out.println(articleResponseDto.getUsername());
            System.out.println(articleResponseDto.getArticleId());
            System.out.println(articleResponseDto.getArticleTitle());
            System.out.println(articleResponseDto.getArticleContent());
            System.out.println(articleResponseDto.getTotalArticleComment());
            System.out.println(articleResponseDto.getCreatedDateTime());
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        List<ArticleCommentResponseDto> articleComments = articleResponseDto.getArticleComments();
        if (!CollectionUtils.isEmpty(articleComments)) {
            for (ArticleCommentResponseDto articleComment : articleComments) {
                System.out.println(articleComment.getUsername());
                System.out.println(articleComment.getArticleCommentId());
                System.out.println(articleComment.getComment());
                System.out.println(articleComment.getCreatedDateTime());
                System.out.println("++++++++++++++++++++++++++++++++++++++");
            }
        } else {
            System.out.println("empty");
        }
    }


}
