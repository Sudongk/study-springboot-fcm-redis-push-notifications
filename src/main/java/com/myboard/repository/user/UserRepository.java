package com.myboard.repository.user;

import com.myboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select user.id from User user where user.username = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);

    @Query("select user from User user where user.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);
}
