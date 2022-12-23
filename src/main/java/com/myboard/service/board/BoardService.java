package com.myboard.service.board;

import com.myboard.dto.requestDto.board.CreateBoardDto;
import com.myboard.dto.requestDto.board.UpdateBoardDto;
import com.myboard.dto.responseDto.board.BoardResponseDto;

import java.util.List;

public interface BoardService {

    Long createBoard(CreateBoardDto createBoardDto, Long userId);

    Long updateBoard(UpdateBoardDto updateBoardDto, Long boardId, Long userId);

    Long deleteBoard(Long boardId, Long userId);

    List<BoardResponseDto> boardList();

    BoardResponseDto boardDetail(Long boardId);
}
