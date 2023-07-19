package com.sparta.sns.service;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.ProfileRequestDto;
import com.sparta.sns.dto.ProfileResponseDto;
import com.sparta.sns.entity.User;
import com.sparta.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j(topic = "Profile")
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileResponseDto myProfile(User user) {
        User loginedUser = findUser(user.getUsername());
        return new ProfileResponseDto(loginedUser);
    }

    @Transactional
    public ApiResponseDto modifyProfile(User user, ProfileRequestDto requestDto) {
        User loginedUser = findUser(user.getUsername());
        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(requestDto.getEmail());
        //다른 이메일로 바꾸는데 그 이메일이
        if (!loginedUser.getEmail().equals(requestDto.getEmail())) {
            if (checkEmail.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        }
        loginedUser.modifyProfile(requestDto);
        return new ApiResponseDto("프로필 수정을 완료했습니다.");
    }

    //user가 db내 존재하는지 검사
    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다.")
        );
    }


}
