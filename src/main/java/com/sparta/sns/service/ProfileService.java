package com.sparta.sns.service;

import com.sparta.sns.config.FileComponent;
import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.ModifyPasswordRequestDto;
import com.sparta.sns.dto.ProfileRequestDto;
import com.sparta.sns.dto.ProfileResponseDto;
import com.sparta.sns.entity.PasswordManager;
import com.sparta.sns.entity.User;
import com.sparta.sns.repository.PasswordManagerRepository;
import com.sparta.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "Profile")
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordManagerRepository passwordManagerRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileComponent fileComponent;

    public ProfileResponseDto userProfile(String username) {
        User loginedUser = findUser(username);
        return new ProfileResponseDto(loginedUser);
    }

    @Transactional
    public ApiResponseDto modifyProfile(User user, MultipartFile file, ProfileRequestDto requestDto) {
        User loginedUser = findUser(user.getUsername());
        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(requestDto.getEmail());
        //다른 이메일로 바꾸는데 그 이메일이 이미 존재하는 경우
        if (!loginedUser.getEmail().equals(requestDto.getEmail())) {
            if (checkEmail.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        }
        // file 비어있지 않으면 imageUrl set
        try {
            if(!file.isEmpty()) {
                String storedFileName = fileComponent.upload(file);
                requestDto.setImageUrl(storedFileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    @Transactional
    public ApiResponseDto modifyPassword(User user, ModifyPasswordRequestDto requestDto) {
        User loginedUser = findUser(user.getUsername());
        //입력한 비밀번호와 DB 내 비밀번호가 동일할 시
        if (passwordEncoder.matches(requestDto.getCurrentPassword(), loginedUser.getPassword())) {

            //가장 최근 사용한 3개의 비밀번호를 가져오기
            List<String> usedPasswords = passwordManagerRepository.findPasswordTopThree(loginedUser);
            System.out.println("로그:"+usedPasswords);
            for(String usedPassword : usedPasswords){

                //바꾸려는 비밀번호가 패스워드관리테이블 내 최근 3개의 비밀번호 중 하나와 일치하는 경우
                if(passwordEncoder.matches(requestDto.getNewPassword(),usedPassword)){
                    System.out.println("비밀번호가 같음.");
                    throw new IllegalArgumentException("최근 3회 이내 사용된 비밀번호로는 바꿀 수 없습니다.");
                }
                System.out.println("비밀번호가 서로 다름.");
            }

            //비밀번호 변경
            String newPassword = passwordEncoder.encode(requestDto.getNewPassword());
            loginedUser.modifyPassword(newPassword);

            //비밀번호 관리테이블에 추가
            PasswordManager passwordManager = new PasswordManager(newPassword,user);
            passwordManagerRepository.save(passwordManager);
            return new ApiResponseDto("비밀번호를 변경했습니다.");
        }
        else
            throw new IllegalArgumentException("입력한 현재 비밀번호가 일치하지 않습니다.");

    }
}
