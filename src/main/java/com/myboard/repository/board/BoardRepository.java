package com.myboard.repository.board;

import com.myboard.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryEx {

    List<Board> findByIdIn(List<Long> boardIds);

    @Query("select b from Board b where b.user.id in (:userIds)")
    List<Board> findAllByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT b FROM Board b WHERE b.id = :boardId AND b.user.id = :userId")
    Optional<Board> findIdByUserIdAndBoardId(@Param("boardId") Long boardId, @Param("userId") Long userId);

}
