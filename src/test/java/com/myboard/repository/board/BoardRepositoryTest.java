package com.myboard.repository.board;

import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.repository.board.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Test
    public void boardList() {
        List<BoardResponseDto> boardResponseDtoList = boardRepository.boardList();
        if (!CollectionUtils.isEmpty(boardResponseDtoList)) {
            for (BoardResponseDto boardResponseDto : boardResponseDtoList) {
                System.out.println(boardResponseDto.getBoardId());
                System.out.println(boardResponseDto.getBoardName());
                System.out.println(boardResponseDto.getCreatedDateTime());
                System.out.println(boardResponseDto.getTotalArticle());
                System.out.println(boardResponseDto.getTotalNewArticle());
                System.out.println("++++++++++++++++++++++++++++++++++++++");
            }
        }
    }

    @Test
    public void boardDetail() {
        BoardResponseDto boardResponseDto = boardRepository.boardDetail(1L);
        if (boardResponseDto != null) {
            System.out.println(boardResponseDto.getBoardId());
            System.out.println(boardResponseDto.getBoardName());
            System.out.println(boardResponseDto.getCreatedDateTime());
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        List<ArticleResponseDto> articleResponseDtoList = boardResponseDto.getArticleResponseDtoList();
        if (CollectionUtils.isEmpty(articleResponseDtoList)) {
            System.out.println("empty");
        } else {
            for (ArticleResponseDto articleResponseDto : articleResponseDtoList) {
                System.out.println(articleResponseDto.getUsername());
                System.out.println(articleResponseDto.getArticleId());
                System.out.println(articleResponseDto.getArticleTitle());
                System.out.println(articleResponseDto.getArticleContent());
                System.out.println(articleResponseDto.getCreatedDateTime());
                System.out.println("++++++++++++++++++++++++++++++++++++++");
            }
        }
    }

}