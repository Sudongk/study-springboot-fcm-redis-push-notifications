package com.myboard.repository.board;

import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.article.QArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.dto.responseDto.board.QBoardResponseDto;
import com.myboard.dto.responseDto.tag.QTagResponseDto;
import com.myboard.dto.responseDto.tag.TagResponseDto;
import com.myboard.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.myboard.entity.QArticle.*;
import static com.myboard.entity.QBoard.*;
import static com.myboard.entity.QTag.*;
import static com.myboard.entity.QUser.*;
import static com.querydsl.core.group.GroupBy.*;
import static com.querydsl.core.group.GroupBy.list;

@RequiredArgsConstructor
public class BoardRepositoryExImpl implements BoardRepositoryEx {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardResponseDto> boardList() {
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
                .orderBy(board.createdAt.desc())
                .groupBy(board.id)
                .fetch();

        List<TagResponseDto> tagResponseDtoList = queryFactory
                .from(board)
                .leftJoin(board.tags, tag)
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

        return boardResponseDtoList;
    }

    @Override
    public BoardResponseDto boardDetail(Long boardId) {
        BoardResponseDto boardResponseDto = queryFactory
                .select(
                        new QBoardResponseDto(
                                board.id.as("boardId"),
                                board.boardName.as("boardName"),
                                board.createdAt.as("createdDateTime")
                        )
                )
                .from(board)
                .where(board.id.eq(boardId))
                .fetchOne();

        List<ArticleResponseDto> articleResponseDtoList = queryFactory
                .select(
                        new QArticleResponseDto(
                                user.username.as("username"),
                                article.id.as("articleId"),
                                article.title.as("articleTitle"),
                                article.viewCount.as("viewCount"),
                                article.createdAt.as("createdDateTime")
                        )
                )
                .from(article)
                .leftJoin(article.user, user)
                .where(article.board.id.eq(boardId))
                .orderBy(article.createdAt.desc())
                .fetch();

        boardResponseDto.setArticleResponseDtoList(articleResponseDtoList);

        return boardResponseDto;
    }
}
