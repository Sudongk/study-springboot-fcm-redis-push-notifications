package com.myboard.service.board;

import com.myboard.dto.requestDto.board.CreateBoardDto;
import com.myboard.dto.requestDto.board.UpdateBoardDto;
import com.myboard.dto.responseDto.article.ArticleResponseDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.entity.Board;
import com.myboard.entity.Tag;
import com.myboard.entity.User;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import com.myboard.service.board.BoardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("게시판 서비스 테스트")
@MockitoSettings
public class BoardServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    private User user;

    private Board board;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("test")
                .password("test")
                .role(User.Role.USER)
                .build();

        this.user.setId(1L);

        this.board = Board.builder()
                .boardName("boardName")
                .user(this.user)
                .build();

        this.board.addTags(Tag.convertListToTags(Arrays.asList("tag1", "tag2")));
    }

    private List<BoardResponseDto> boardListResponse() {
        List<BoardResponseDto> response = new ArrayList<>();

        BoardResponseDto firstBoard = BoardResponseDto.builder()
                .boardId(1L)
                .boardName("test")
                .createdDateTime(LocalDateTime.now())
                .totalArticle(10L)
                .totalNewArticle(1L)
                .build();

        BoardResponseDto secondBoard = BoardResponseDto.builder()
                .boardId(2L)
                .boardName("test")
                .createdDateTime(LocalDateTime.now())
                .totalArticle(10L)
                .totalNewArticle(1L)
                .build();

        response.add(firstBoard);
        response.add(secondBoard);

        return response;
    }

    private BoardResponseDto boardDetailResponse() {
        List<ArticleResponseDto> articleResponseList = new ArrayList<>();

        ArticleResponseDto firstArticle = ArticleResponseDto.builder()
                .username("test")
                .articleId(1L)
                .articleTitle("test")
                .viewCount(1L)
                .createdDateTime(LocalDateTime.now())
                .build();

        ArticleResponseDto secondArticle = ArticleResponseDto.builder()
                .username("test2")
                .articleId(2L)
                .articleTitle("test2")
                .viewCount(2L)
                .createdDateTime(LocalDateTime.now())
                .build();

        articleResponseList.add(firstArticle);
        articleResponseList.add(secondArticle);

        BoardResponseDto response = BoardResponseDto.builder()
                .boardId(1L)
                .boardName("test")
                .createdDateTime(LocalDateTime.now())
                .build();

        response.setArticleResponseDtoList(articleResponseList);

        return response;
    }

    private CreateBoardDto getCreateBoardDto() {
        return CreateBoardDto.builder()
                .boardName("test")
                .tagNames(Arrays.asList("test1", "test2"))
                .build();
    }

    private UpdateBoardDto getUpdateBoardDto() {
        return UpdateBoardDto.builder()
                .boardName("boardName")
                .tagNames(Arrays.asList("tag1", "tag2"))
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 생성 성공")
    void createBoardSuccessful() throws Exception {
        // given
        CreateBoardDto request = getCreateBoardDto();

        given(userRepository.getReferenceById(USER_ID))
                .willReturn(user);

        given(boardRepository.save(any()))
                .willReturn(board);

        // when
        boardService.createBoard(request, USER_ID);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(any());

        then(boardRepository).should(times(1))
                .save(any(Board.class));
    }

    @Test
    @WithMockUser
    @DisplayName("존재하지 않는 유저는 유저일 경우 게시판 생성 실패")
    void whenUserNotExist() throws Exception {
        // given
        CreateBoardDto request = getCreateBoardDto();

        given(userRepository.getReferenceById(USER_ID))
                .willThrow(UsernameNotFoundException.class);

        // when
        assertThatThrownBy(() -> boardService.createBoard(request, USER_ID))
                .isInstanceOf(UsernameNotFoundException.class);

        // then
        then(userRepository).should(times(1))
                .getReferenceById(USER_ID);

        then(boardRepository).should(never())
                .save(board);
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 수정 성공")
    void updateBoardSuccessful() throws Exception {
        // given
        UpdateBoardDto request = getUpdateBoardDto();

        given(boardRepository.findIdByUserIdAndBoardId(BOARD_ID, USER_ID))
                .willReturn(Optional.of(board));

        // when
        boardService.updateBoard(request, BOARD_ID, USER_ID);

        // then
        then(boardRepository).should(times(1))
                .findIdByUserIdAndBoardId(any(), any());

        then(boardRepository).should(times(1))
                .flush();
    }

    @Test
    @WithMockUser
    @DisplayName("로그인한 유저 소유의 게시판이 아닌 경우 업데이트 실패")
    void whenBoardIsNotBelongsToUserShouldNeverUpdate() throws Exception {
        // given
        UpdateBoardDto request = getUpdateBoardDto();

        given(boardRepository.findIdByUserIdAndBoardId(BOARD_ID, USER_ID))
                .willThrow(NotAuthorException.class);

        // when
        assertThatThrownBy(() -> boardService.updateBoard(request, BOARD_ID, USER_ID))
                .isInstanceOf(NotAuthorException.class);

        // then
        then(boardRepository).should(times(1))
                .findIdByUserIdAndBoardId(any(), any());

        then(boardRepository).should(never())
                .flush();
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 삭제 성공")
    void deleteBoardSuccessful() throws Exception {
        // given
        given(boardRepository.findIdByUserIdAndBoardId(BOARD_ID, USER_ID))
                .willReturn(Optional.of(board));

        // when
        boardService.deleteBoard(BOARD_ID, USER_ID);

        // then
        then(boardRepository).should(times(1))
                .findIdByUserIdAndBoardId(any(), any());

        then(boardRepository).should(times(1))
                .deleteById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("로그인한 유저 소유의 게시판이 아닌 경우 삭제 실패")
    void whenBoardIsNotBelongUser_shouldNeverDelete() throws Exception {
        // given
        given(boardRepository.findIdByUserIdAndBoardId(BOARD_ID, USER_ID))
                .willThrow(NotAuthorException.class);

        // when
        assertThatThrownBy(() -> boardService.deleteBoard(BOARD_ID, USER_ID))
                .isInstanceOf(NotAuthorException.class);

        // then
        then(boardRepository).should(times(1))
                .findIdByUserIdAndBoardId(any(), any());

        then(boardRepository).should(never())
                .deleteById(any());
    }

    @Test
    @WithMockUser
    @DisplayName("게시판 리스트 리턴")
    void boardList() {
        // given
        List<BoardResponseDto> response = boardListResponse();

        given(boardRepository.boardList())
                .willReturn(response);

        // when
        boardService.boardList();

        // then
        then(boardRepository).should(times(1)).boardList();
    }

    @Test
    @WithMockUser
    @DisplayName("특정 게시판에 존재하는 게시글 리스트 리턴")
    void boardDetail() {
        // given
        BoardResponseDto response = boardDetailResponse();

        given(boardRepository.boardDetail(BOARD_ID))
                .willReturn(response);

        // when
        boardService.boardDetail(BOARD_ID);

        // then
        then(boardRepository).should(times(1)).boardDetail(any());
    }
}
