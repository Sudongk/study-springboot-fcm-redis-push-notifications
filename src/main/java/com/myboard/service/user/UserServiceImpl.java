package com.myboard.service.user;

import com.myboard.dto.requestDto.user.UserCreateRequestDto;
import com.myboard.entity.User;
import com.myboard.exception.user.SelfConfirmationException;
import com.myboard.exception.user.UserNameDuplicatedException;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public Long userCreate(UserCreateRequestDto userCreateRequestDto) {
        isUserNameDuplicated(userCreateRequestDto.getUsername());

        User newUser = User.builder()
                .username(userCreateRequestDto.getUsername())
                .password(userCreateRequestDto.getPassword())
                .role(User.Role.USER)
                .build();

        newUser.encodePassword(bCryptPasswordEncoder);

        User savedUser = userRepository.save(newUser);

        return savedUser.getId();
    }

    @Override
    @Transactional
    public Long userDelete(Long userId, Long currentUserId) {
        isUserIdOwnedByCurrentUser(userId, currentUserId);
        userRepository.deleteById(userId);
        return userId;
    }

    private void isUserNameDuplicated(String username) {
        Optional<Long> userId = userRepository.findIdByUsername(username);

        if (userId.isPresent()) {
            throw new UserNameDuplicatedException();
        }

    }

    private void isUserIdOwnedByCurrentUser(Long userId, Long currentUserId) {
        if (!Objects.equals(currentUserId, userId)) {
            throw new SelfConfirmationException();
        }
    }

}