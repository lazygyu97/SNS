package com.sparta.sns.controller;

import com.sparta.sns.dto.*;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import com.sparta.sns.service.ProfileService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j // log 기능 가져오는 어노테이션
//@Controller // --> String
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;
    private final PostService postService;

    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(ex.getMessage()));
    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {

        String username=userDetails.getUsername();
        String nickname=userDetails.getNickname();
        String email=userDetails.getUser().getEmail();
        String oneLine=userDetails.getUser().getOneLine();
        String image=userDetails.getUser().getImage();
        System.out.println(image);

        if(!postService.getPostById(userDetails.getUser().getId()).isEmpty()){
            List<PostResponseDto> postList= postService.getPostById(userDetails.getUser().getId());
            model.addAttribute("postList",postList);

        }
        if(image==null){
            image="/images/user.png";
        }
        model.addAttribute("username",username);
        model.addAttribute("image",image);
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
            return "redirect:/api/mypage";
        }

        if(profileResponseDto.getImage()==null){
            profileResponseDto.setImage("/images/user.png");
        }

        List<PostResponseDto> postList= postService.getPostByUsername(username);
        model.addAttribute("profile",profileResponseDto);
        model.addAttribute("postList",postList);
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

    @PutMapping("/myprofile/image")
    public ResponseEntity<ApiResponseDto> updateImage(@RequestPart(value = "file") MultipartFile image, @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("어디까지 왔나~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(image);
        profileService.updateImage(image,userDetails.getUser());


        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("프로필 수정 성공"));
    }

    @GetMapping("/myprofile/password")
    public String changePasswordPage(){
        return "changepwd";
    }
//   <img th:onclick="'editPost(' + ${postList.getId()} + ')'" style="width: 20px;height: 20px"
//    src="/images/edit.png" value="">
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