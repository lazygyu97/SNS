package com.sparta.sns.controller;

import com.sparta.sns.dto.*;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import com.sparta.sns.service.ProfileService;
import com.sparta.sns.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // log 기능 가져오는 어노테이션
//@Controller // --> String
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final PostService postService;

    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(ex.getMessage()));
    }

    @GetMapping("/userprofile/mypage")
    public String myPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {

        String username=userDetails.getUsername();
        String nickname=userDetails.getNickname();
        String email=userDetails.getUser().getEmail();
        String oneLine=userDetails.getUser().getOneLine();
        List<PostResponseDto> postList= postService.getPostByUsername(userDetails.getUser().getId());
        model.addAttribute("username",username);
        model.addAttribute("postList",postList);
        model.addAttribute("nickname",nickname);
        model.addAttribute("email",email);
        model.addAttribute("oneLine",oneLine);
        return "mypage";
    }

    @GetMapping("/userprofile/{username}")
    public String userPage(Model model, @PathVariable String username,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProfileResponseDto profileResponseDto = profileService.userProfile(username);

        //본인 페이지로 이동시도할 시, myProfile로 이동
        if(username.equals(userDetails.getUsername())){
            String nickname=userDetails.getNickname();
            String email=userDetails.getUser().getEmail();
            String oneLine=userDetails.getUser().getOneLine();
            List<PostResponseDto> postList= postService.getPostByUsername(userDetails.getUser().getId());

            model.addAttribute("postList",postList);
            model.addAttribute("nickname",nickname);
            model.addAttribute("email",email);
            model.addAttribute("oneLine",oneLine);
            return "mypage";
        }
        model.addAttribute("profile",profileResponseDto);
        return "userpage";
    }

    @PutMapping("/myprofile")
    public ResponseEntity<ApiResponseDto> modifyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ProfileRequestDto requestDto, BindingResult bindingResult) {
        //validation 예외처리
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

        return ResponseEntity.ok().body(profileService.modifyProfile(userDetails.getUser(), requestDto));
    }

    @GetMapping("/myprofile/password")
    public String changePasswordPage(){
        return "changepwd";
    }

    @PutMapping("/myprofile/password")
    public ResponseEntity<ApiResponseDto> modifyPassword(@AuthenticationPrincipal UserDetailsImpl userDetails,@Valid @RequestBody ModifyPasswordRequestDto requestDto, BindingResult bindingResult) {
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
        return ResponseEntity.status(201).body(profileService.modifyPassword(userDetails.getUser(), requestDto));
    }
}