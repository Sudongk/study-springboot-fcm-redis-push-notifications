package com.myboard.repository.board;

import com.myboard.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryEx {

    @Query("SELECT b FROM Board b WHERE b.id = :boardId AND b.user.id = :userId")
    Optional<Board> findIdByUserIdAndBoardId(@Param("boardId") Long boardId, @Param("userId") Long userId);

}
