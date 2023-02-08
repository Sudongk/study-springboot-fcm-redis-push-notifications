package com.myboard.repository.board;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.repository.RepositoryExTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@Import(TestQuerydslConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardRepositoryExTest extends RepositoryExTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시판 리스트 조회시 List<BoardResponseDto> 반환")
    void test() {
        // given

        // when
        List<BoardResponseDto> responseList = boardRepository.boardList();
        for (BoardResponseDto responseDto : responseList) {
            System.out.println("board id : " + responseDto.getBoardId());
        }

        // then
        assertThat(responseList).hasSize(1);
        assertThat(responseList).extracting(BoardResponseDto::getBoardId)
                .contains(1L);
    }

}
