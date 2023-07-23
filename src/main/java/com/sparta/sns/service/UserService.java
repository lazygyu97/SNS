package com.sparta.sns.service;

import com.sparta.sns.dto.*;
import com.sparta.sns.entity.PasswordManager;
import com.sparta.sns.entity.SignupAuth;
import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.repository.PasswordManagerRepository;
import com.sparta.sns.repository.SignupAuthRepository;
import com.sparta.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupAuthRepository signupAuthRepository;
    private final PasswordManagerRepository passwordManagerRepository;
    private final JavaMailSender mailSender;


    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public ApiResponseDto signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();
        String email = requestDto.getEmail();
        // 아이디 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        // 회원가입 이메일 인증번호 검증여부 확인
        Optional<SignupAuth> checkSignupAuth = signupAuthRepository.findByEmail(email);

        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }

        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        //이메일 인증 정보가 Table에 없거나, 상태코드가 1(OK)이 아닌경우
        if (checkSignupAuth.isEmpty() || checkSignupAuth.get().getAuthStatus() != 1) {
            throw new IllegalArgumentException("이메일 인증이 수행되지 않았습니다. 이메일 인증을 완료해주세요.");
        }
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;//user로 바꿔서 사용
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }
        // 사용자 등록
        User user = new User(nickname, username, password, email, role);
        userRepository.save(user);
        // 비밀번호 관리 테이블에 등록
        PasswordManager passwordManager = new PasswordManager(password, user);
        passwordManagerRepository.save(passwordManager);
        return new ApiResponseDto("회원가입 완료");
    }

    //회원가입 시 아이디 중복확인
    public ApiResponseDto checkUsername(UsernameRequestDto requestDto) {
        findUser(requestDto.getUsername()); // 동일 아이디가 있을 시, 중복된 사용자가 존재한다는 Exception 반환

        return new ApiResponseDto("사용 가능한 아이디입니다.");
    }

    public boolean findUser(String username) {
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) { //isPresent() --> 데이터에 동일 데이터가 있는지 확인.
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        return true;
    }

    //회원가입을 위한 이메일 인증 메서드
    //1. 이미 회원가입된 이메일이 있는지 확인
    //2. 이전에 인증번호요청이력이 있는지 확인. 있으면 삭제
    //3.인증번호 발급,메일 보내기
    //4. signupauth 테이블에 이메일정보 & 인증키(authKey) 저장(authStatus = default 0, createdAt = 생성일시)
    //5. 메일에서 받은 인증번호 데이터를 입력했을 때 일치하는 경우, authStatus = 1로 업데이트 (인증 완료.)
    //6. 회원가입 최종 단계에서 검증 authStatus = 1 여부 확인 - (signup메서드에 적용)
    @Transactional
    public ApiResponseDto authentication(AuthenticationRequestDto requestDto) {
        //0. 인증 테이블 내 인증시간이 만료된 데이터가 있으면 삭제
        signupAuthRepository.deleteExpiredSignupAuth();

        //1. 이미 회원가입된 이메일이 있는지 확인
        User targetUser = userRepository.findByEmail(requestDto.getEmail()).orElse(null);
        if (targetUser != null) {
            throw new IllegalArgumentException("이미 가입한 사용자의 이메일입니다. 다른 이메일을 입력해주세요.");
        }

        //2. 이전에 인증번호요청이력이 있는지 확인. 있으면 삭제
        SignupAuth targetSignupAuth = signupAuthRepository.findByEmail(requestDto.getEmail()).orElse(null);
        if (targetSignupAuth != null) {
            signupAuthRepository.delete(targetSignupAuth);
        }

        //3.인증번호 발급, 메일 보내기
        String authKey = getTempCode();
        authenticationEmailSend(authKey, requestDto.getEmail());

        //4.인증 DB  테이블에 이메일정보 & 인증키(authKey) 저장(status = default 0)
        SignupAuth signupAuth = new SignupAuth(requestDto.getEmail(), authKey);
        signupAuthRepository.save(signupAuth);
        return new ApiResponseDto("입력하신 이메일로 인증번호를 전송했습니다.");
    }

    //회원가입 인증번호를 이메일전송하는 메서드
    private void authenticationEmailSend(String authKey, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("CHECK SHIRTS 회원가입 이메일 인증번호");
        message.setText("안녕하세요? CHECK SHIRTS에서 이메일 인증번호를 안내드립니다.\n 인증번호 :" + authKey + "\n해당 인증번호를 복사하여 회원가입 홈페이지에서 이메일 인증을 완료해주세요.");
        mailSender.send(message);
    }

    @Transactional
    public ApiResponseDto verification(VerificationRequestDto requestDto) {
        //DB 저장된 회원가입 인증번호, 이메일 가져오기
        SignupAuth targetSignupAuth = signupAuthRepository.findByEmail(requestDto.getEmail()).orElse(null);
        if (targetSignupAuth == null) {
            throw new IllegalArgumentException("인증번호가 발급되지 않은 상태입니다. 인증번호 발급 버튼을 먼저 눌러주세요.");
        }

        //인증시간이 만료된 경우(생성 후 5분이 지났을 경우)   시간A.isBefore(시간 B) : A가 B보다 과거일 때 true
        if (targetSignupAuth.getExpirationTime().isBefore(LocalDateTime.now())) {
            targetSignupAuth.changeStatusNO(); // 인증번호 상태값 0으로 변경
            signupAuthRepository.save(targetSignupAuth); //변경상태를 DB 저장
            throw new IllegalArgumentException("인증번호 발급 후 5분이 경과하였습니다. 인증번호를 다시 발급해주세요.");
        }

        //인증번호 대조 - 일치
        if (targetSignupAuth.getAuthKey().equals(requestDto.getAuthKey())) {
            System.out.println("인증번호 일치");
            targetSignupAuth.changeStatusOK(); // 인증번호 상태값 1로 변경
            signupAuthRepository.save(targetSignupAuth); //변경상태를 DB 저장
            return new ApiResponseDto("인증번호 확인이 완료되었습니다.");
        } else {//인증번호 대조 - 불일치
            targetSignupAuth.changeStatusNO(); // 인증번호 상태값 0으로 변경
            signupAuthRepository.save(targetSignupAuth); //변경상태를 DB 저장
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
    }

    //랜덤함수로 랜덤값 만들기
    //A~Z,0~9 중 10개를 뽑아 임시 비밀번호 /회원가입 이메일 인증번호 생성
    public String getTempCode() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    //전체 유저 리스트 가져오기
    public List<User> getAllUsers() {

        if (userRepository.findAll().isEmpty()) {
            throw new IllegalArgumentException("회원가입한 유저가 없습니다.");
        }
        return userRepository.findAll();
    }


}