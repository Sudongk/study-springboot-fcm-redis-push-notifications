package com.myboard.service.board;

import com.myboard.dto.requestDto.board.CreateBoardDto;
import com.myboard.dto.requestDto.board.UpdateBoardDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.entity.Board;
import com.myboard.entity.Tag;
import com.myboard.entity.User;
import com.myboard.exception.board.BoardNotFoundException;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.exception.user.UserNotFoundException;
import com.myboard.repository.board.BoardRepository;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    // 엔티티 겍체 자체가 아니라 엔티티에 대한 참조만 필요한 경우 getReference 메서드 아니면 항상 find 메서드를 사용 권장
    // getReference 메서드를 통해 실행된 쿼리 수와 영속성 컨텍스트의 메모리 공간이 줄어들어 애플리케이션의 성능 향상
    public Long createBoard(CreateBoardDto createBoardDto, Long userId) {
        User userRef = getUserRef(userId);

        List<Tag> tags = Tag.convertListToTags(createBoardDto.getTagNames());

        Board board = Board.builder()
                .boardName(createBoardDto.getBoardName())
                .user(userRef)
                .build();

        board.addTags(tags);
        Board savedBoard = boardRepository.save(board);

        return savedBoard.getId();
    }

    @Override
    @Transactional
    public Long updateBoard(UpdateBoardDto updateBoardDto, Long boardId, Long userId) {
        Board board = isBoardOwnedByUser(boardId, userId);

        List<Tag> oldTags = board.getTags();
        List<Tag> newTags = Tag.convertListToTags(updateBoardDto.getTagNames());

        // 이전 태그가 없을시 새로운 태그 update
        if (oldTags.isEmpty()) {
            board.addTags(newTags);
        }

        // 변경된 태그가 있을시 update 또는 기존 태그에서 몇개의 태그를 삭제할시 update
        if (!oldTags.containsAll(newTags) || oldTags.size() != newTags.size()) {
            board.clearAndAddNewTags(newTags);
        }

        board.updateBoardName(updateBoardDto.getBoardName());

        boardRepository.flush();

        return boardId;
    }

    @Override
    @Transactional
    public Long deleteBoard(Long boardId, Long userId) {
        Board board = isBoardOwnedByUser(boardId, userId);
        boardRepository.deleteById(boardId);
        return board.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDto> boardList() {
        return boardRepository.boardList();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDto boardDetail(Long boardId) {
        return boardRepository.boardDetail(boardId);
    }

    private User getUserRef(Long userId) {
        return Optional.of(userRepository.getReferenceById(userId))
                .orElseThrow(UserNotFoundException::new);
    }

    private Board isBoardOwnedByUser(Long boardId, Long userId) {
        return boardRepository.findIdByUserIdAndBoardId(boardId, userId)
                .orElseThrow(NotAuthorException::new);
    }

}
