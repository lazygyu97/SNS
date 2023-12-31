package com.sparta.sns.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.sns.dto.*;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.jwt.JwtUtil;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.KakaoService;
import com.sparta.sns.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j // log 기능 가져오는 어노테이션
//@Controller // --> String
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    //의존성 주입
    private final UserService userService;
    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    //예외처리 메서드
    //컨트롤러 내 API가 호출되다가 Exception 발생 시, 코드 실행
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(ex.getMessage()));
    }
    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
//        //validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();

        //validation 예외가 1건 이상인 경우
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
                errorMessage.append(fieldError.getDefaultMessage()).append(" ");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.signup(requestDto));
    }
    //입력한 이메일정보를 받아 인증번호를 발급
    @PostMapping("/user/signup/username")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> checkUsername(@RequestBody UsernameRequestDto requestDto){
        return ResponseEntity.ok().body(userService.checkUsername(requestDto));
    }

    //입력한 이메일정보를 받아 인증번호를 발급
    @PostMapping("/user/signup/authentication")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> authentication(@RequestBody AuthenticationRequestDto requestDto){
        return ResponseEntity.ok().body(userService.authentication(requestDto));
    }
    // 입력한 이메일과 인증번호를 받아 DB 내 인증번호와 대조 및 만료여부 검증
    @PostMapping("/user/signup/verification")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> verification(@RequestBody VerificationRequestDto requestDto){
            return ResponseEntity.ok().body(userService.verification(requestDto));
    }
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        //로그인 작업이 끝난 Jwt토큰을 받음
        String token = kakaoService.kakaoLogin(code);
        // Cookie Value 에는 공백이 불가능해서 encoding 진행
        token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
        // 쿠키에 생성된 JWT토큰을 넣어줌
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token);
        cookie.setPath("/");
        //response에 쿠키를 담아 반환
        response.addCookie(cookie);

        return "redirect:/";
    }

    // 3. 로그아웃 기능 : 구현해보고 싶어서 하긴했는데 확인이 필요함.
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {
        // 현재 사용자의 토큰을 가져오기
        String token = jwtUtil.resolveToken(request);
        log.info(token);
        if (token == null) {
            throw new IllegalArgumentException("로그인 상태가 아닙니다.");
        }
        // 토큰을 블랙리스트에 추가하여 만료시키기
        jwtUtil.addTokenToBlacklist(token);
        // 쿠키에서 토큰 제거
        jwtUtil.removeTokenFromCookie(response);

        return ResponseEntity.ok(new ApiResponseDto("로그아웃 되었습니다."));
    }

    //회원 차단
    @DeleteMapping("/user/deny/{id}")
    public ResponseEntity<ApiResponseDto> denyUser(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        if (!(userDetails.getUser().getRole() == UserRoleEnum.ADMIN)){
            throw new IllegalArgumentException("관리자만 사용가능한 기능입니다.");
        }
        userService.denyUser(id);

        return ResponseEntity.ok(new ApiResponseDto("회원 차단 성공"));
    }
    //회원 차단 해제
    @PutMapping("/user/deny/{id}")
    public ResponseEntity<ApiResponseDto> denyCancelUser(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        if (!(userDetails.getUser().getRole() == UserRoleEnum.ADMIN)){
            throw new IllegalArgumentException("관리자만 사용가능한 기능입니다.");
        }
        userService.denyCancelUser(id);

        return ResponseEntity.ok(new ApiResponseDto("회원 차단 해제 성공"));
    }



}