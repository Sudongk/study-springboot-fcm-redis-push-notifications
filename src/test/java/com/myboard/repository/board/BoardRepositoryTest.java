package com.myboard.repository.board;

import com.myboard.entity.*;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.articleComment.ArticleCommentRepository;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    // Querydsl 테스트 용도도
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        this.em = testEntityManager.getEntityManager();

        this.user = User.builder()
                .username("username")
                .password("password")
                .role(User.Role.USER)
                .build();

        this.user.setId(1L);

        userRepository.save(user);
    }

    private Board getBoard() {
        return Board.builder()
                .boardName("boardName")
                .user(this.user)
                .build();
    }

    private List<Board> getBoardList() {

        List<Board> boardList = new ArrayList<>();

        Board board1 = getBoard();
        Board board2 = getBoard();
        Board board3 = getBoard();

        boardList.add(board1);
        boardList.add(board2);
        boardList.add(board3);

        return boardList;
    }

    @Test
    @DisplayName("게시판 저장 성공")
    void boardSave() {
        // given
        Board board = getBoard();

        // when
        Board savedBoard = boardRepository.save(board);

        testEntityManager.flush();

        // then
        assertThat(savedBoard).isSameAs(board);
        assertThat(savedBoard.getId()).isNotNull();
        assertThat(savedBoard.getBoardName()).isNotNull();
        assertThat(savedBoard.getCreatedAt()).isNotNull();
        assertThat(savedBoard.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시판 아이디로 조회 성공")
    void boardFindById() {
        // given
        Board board = getBoard();

        // when
        Board savedBoard = boardRepository.save(board);

        // then
        Optional<Board> findBoard = boardRepository.findById(savedBoard.getId());
        assertThat(findBoard).containsSame(savedBoard);
    }

    @Test
    @DisplayName("게시판에 작성자 아이디 존재 여부 체크(외래키 존재 여부)")
    void checkBoard_userId() {
        // given
        Board board = getBoard();

        Board savedBoard = boardRepository.save(board);
        // when
        Optional<Board> findBoard = boardRepository.findById(savedBoard.getId());

        // then
        assertThat(findBoard).isPresent();
        assertThat(findBoard.get().getUser()).isNotNull();
    }

    @Test
    @DisplayName("게시판 리스트 반환")
    void returnBoardListByIds() {
        // given
        List<Board> getBoardList = getBoardList();

        List<Board> boardList = boardRepository.saveAllAndFlush(getBoardList);

        List<Long> boardIds = boardList.stream()
                .map(BaseColumn::getId)
                .collect(Collectors.toList());

        testEntityManager.clear();

        // when
        List<Board> findBoardList = boardRepository.findByIdIn(boardIds);

        // then
        assertThat(findBoardList).hasSize(boardList.size());
    }

    @Test
    @DisplayName("사용자 아이디로 게시판 리스트 반환")
    void returnBoardListByUserIds() {
        // given
        List<Board> getBoardList = getBoardList();

        List<Board> boardList = boardRepository.saveAllAndFlush(getBoardList);

        List<Long> userIds = boardList.stream()
                .map(board -> board.getUser().getId())
                .collect(Collectors.toList());

        testEntityManager.clear();

        // when
        List<Board> findBoardList = boardRepository.findAllByUserIds(userIds);

        // then
        assertThat(findBoardList).hasSize(boardList.size());
    }
}