package com.myboard.repository.articleComment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArticleCommentRepositoryTest {

    @Autowired
    ArticleCommentRepository articleCommentRepository;

    @Test
    void findIdByUserIdAndArticleId() {
        Optional<Long> idByUserIdAndArticleCommentId = articleCommentRepository.findIdByUserIdAndArticleCommentId(1L, 1L);
        idByUserIdAndArticleCommentId.ifPresent(System.out::println);
    }


}