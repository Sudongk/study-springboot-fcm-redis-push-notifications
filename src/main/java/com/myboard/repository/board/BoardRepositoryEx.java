package com.myboard.repository.board;

import com.myboard.dto.responseDto.board.BoardResponseDto;

import java.util.List;

public interface BoardRepositoryEx {
    List<BoardResponseDto> boardList();
    BoardResponseDto boardDetail(Long boardId);
}
