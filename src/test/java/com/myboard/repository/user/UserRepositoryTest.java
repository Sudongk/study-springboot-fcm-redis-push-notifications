package com.myboard.repository.user;

import com.myboard.config.TestQuerydslConfig;
import com.myboard.entity.Article;
import com.myboard.entity.Board;
import com.myboard.entity.Tag;
import com.myboard.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestQuerydslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void setUp() {

    }

    private User getUser() {
        return User.builder()
                .username("username")
                .password("password")
                .role(User.Role.USER)
                .build();
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void saveUser() {
        // given
        User user = getUser();

        // when
        User savedUser = userRepository.save(user);

        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertThat(savedUser).isSameAs(user);
        assertThat(savedUser.getUsername()).isNotNull();
        assertThat(savedUser.getPassword()).isNotNull();
        assertThat(savedUser.getRole()).isNotNull();
    }

    @Test
    @DisplayName("ID로 사용자 조회")
    void userFindById() {
        // given
        User user = getUser();
        User savedUser = userRepository.save(user);

        testEntityManager.flush();

        // when
        Optional<User> findUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(findUser).containsSame(savedUser);
    }

    @Test
    @DisplayName("사용자 이름으로 조회")
    void findUserByUsername() {
        // given
        User user = getUser();
        User savedUser = userRepository.save(user);

        testEntityManager.flush();

        // when
        Optional<User> findUser = userRepository.findUserByUsername(savedUser.getUsername());

        // then
        assertThat(findUser).containsSame(savedUser);
    }

    @Test
    @DisplayName("사용자 이름으로 사용자 ID 조회")
    void findIdByUsername() {
        // given
        User user = getUser();
        User savedUser = userRepository.save(user);

        testEntityManager.flush();

        // when
        Optional<Long> findUserId = userRepository.findIdByUsername(savedUser.getUsername());

        // then
        assertThat(findUserId.get()).isSameAs(savedUser.getId());
        assertThat(findUserId.get()).isEqualTo(savedUser.getId());
    }
}
