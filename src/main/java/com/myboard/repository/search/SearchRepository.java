package com.myboard.repository.search;

import com.myboard.dto.requestDto.search.SearchParameter;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.article.QArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.board.QBoardResponseDto;
import com.myboard.dto.responseDto.tag.QTagResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
import com.myboard.entity.QArticle;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.myboard.entity.QArticle.*;
import static com.myboard.entity.QArticleComment.articleComment;
import static com.myboard.entity.QBoard.board;
import static com.myboard.entity.QTag.tag;
import static com.myboard.entity.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class SearchRepository {

    private final JPAQueryFactory queryFactory;

    public Page<BoardResponseDto> searchBoard(SearchParameter searchParameter, Pageable pageable) {
        QArticle article1 = new QArticle("article1");
        QArticle article2 = new QArticle("article2");

        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));

        List<BoardResponseDto> boardResponseDtoList = queryFactory
                .select(
                        new QBoardResponseDto(
                                board.id.as("boardId"),
                                board.boardName.as("boardName"),
                                board.createdAt.as("createdDateTime"),
                                article1.id.count().as("totalArticle"),
                                article2.id.count().as("totalNewArticle")
                        )
                )
                .from(board)
                .leftJoin(article1)
                .on(board.id.eq(article1.board.id))
                .leftJoin(article2)
                .on(article1.id.eq(article2.id), article2.createdAt.goe(now))
                .where(boardContainsKeyword(searchParameter))
                .orderBy(board.createdAt.desc())
                .groupBy(board.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> boardIdList = boardResponseDtoList.stream()
                .map(BoardResponseDto::getBoardId)
                .collect(Collectors.toList());

        List<TagResponseDto> tagResponseDtoList = queryFactory
                .from(board)
                .leftJoin(board.tags, tag)
                .where(board.id.in(boardIdList))
                .transform(
                        groupBy(board.id.as("boardId")).list(
                                new QTagResponseDto(
                                        board.id.as("boardId"),
                                        list(tag.name.as("tagName"))
                                )
                        )
                );

        Map<Long, List<TagResponseDto>> collect = tagResponseDtoList.stream()
                .collect(Collectors.groupingBy(TagResponseDto::getBoardId));

        boardResponseDtoList.forEach(
                dto -> dto.setTags(collect.get(dto.getBoardId()))
        );

        return new PageImpl<>(boardResponseDtoList, pageable, boardIdList.size());
    }

    public Page<ArticleResponseDto> searchArticle(SearchParameter searchParameter, Pageable pageable) {
        List<ArticleResponseDto> articleResponseDtoList = queryFactory
                .select(
                        new QArticleResponseDto(
                                user.username.as("username"),
                                article.id.as("articleId"),
                                article.title.as("articleTitle"),
                                article.content.as("articleContent"),
                                article.viewCount.as("viewCount"),
                                article.createdAt.as("createdDateTime"),
                                articleComment.id.count().as("totalArticleComment")
                        )
                )
                .from(article)
                .leftJoin(article.user, user)
                .leftJoin(article.articleCommentList, articleComment)
                .where(articleContainsKeyword(searchParameter))
                .groupBy(article.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(articleResponseDtoList, pageable, articleResponseDtoList.size());
    }

    private BooleanExpression articleContainsKeyword(SearchParameter searchParameter) {
        return article.title.contains(searchParameter.getSearchKeyword().getValue());
    }

    private BooleanExpression boardContainsKeyword(SearchParameter searchParameter) {
        return board.boardName.contains(searchParameter.getSearchKeyword().getValue());
    }

}
