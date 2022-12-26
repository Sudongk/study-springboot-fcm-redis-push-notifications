package com.myboard.controller.board;

import com.myboard.aop.resolver.CurrentLoginUserId;
import com.myboard.aop.valid.CheckExist;
import com.myboard.aop.valid.EntityType;
import com.myboard.dto.requestDto.board.CreateBoardDto;
import com.myboard.dto.requestDto.board.UpdateBoardDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;
import com.myboard.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class BoardController {

    private final BoardService boardService;

    /**
     * 보드 리스트
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/board")
    public ResponseEntity<List<BoardResponseDto>> boardList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.boardList());
    }

    /**
     * 보드 생성
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/board/create")
    public ResponseEntity<Long> createBoard(@Valid @RequestBody CreateBoardDto createBoardDto,
                                            @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(createBoardDto, userId));
    }

    /**
     * 보드 수정
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/board/{boardId}/update")
    public ResponseEntity<Long> updateBoard(@Valid @RequestBody UpdateBoardDto updateBoardDto,
                                            @PathVariable("boardId") Long boardId,
                                            @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.updateBoard(updateBoardDto, boardId, userId));
    }

    /**
     * 보드 삭제
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/board/{boardId}/delete")
    public ResponseEntity<?> deleteBoard(@CheckExist(type = EntityType.BOARD, message = "B003") @PathVariable("boardId") Long boardId,
                                         @CurrentLoginUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.deleteBoard(boardId, userId));
    }

    /**
     * 보드 상세 조회
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<BoardResponseDto> boardDetail(@CheckExist(type = EntityType.BOARD, message = "B003") @PathVariable("boardId") Long boardId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardService.boardDetail(boardId));
    }

}
