package com.myboard.aop.valid;

import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Slf4j
@Component
@RequiredArgsConstructor
public class CheckExistValidator implements ConstraintValidator<CheckExist, Long> {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    private EntityType entityType;

    @Override
    public void initialize(CheckExist checkExist) {
        log.info("entityType : {}", checkExist.type());
        this.entityType = checkExist.type();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        log.info("value : {}", value);
        boolean result = false;

        switch (entityType) {
            case USER:
                result = isUserExist(value);
                break;

            case BOARD:
                result = isBoardExist(value);
                break;

            case ARTICLE:
                result = isArticleExist(value);
                break;

            case COMMENT:
                result = isCommentExist(value);
                break;

            default:
                break;
        }

        return result;
    }

    private boolean isCommentExist(Long value) {
        return articleCommentRepository.existsById(value);
    }

    private boolean isArticleExist(Long value) {
        return articleRepository.existsById(value);
    }

    private boolean isBoardExist(Long value) {
        return boardRepository.existsById(value);
    }

    private boolean isUserExist(Long value) {
        return userRepository.existsById(value);
    }
}
