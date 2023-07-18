package com.sparta.sns.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.sns.dto.SignupRequestDto;
import com.sparta.sns.jwt.JwtUtil;
import com.sparta.sns.service.KakaoService;
import com.sparta.sns.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/main")
    public String mainPage(){return "main";}

    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        System.out.println(requestDto.getEmail());
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
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
}