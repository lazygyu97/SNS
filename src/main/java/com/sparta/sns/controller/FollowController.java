package com.sparta.sns.controller;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.FollowUserResponseDto;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // log 기능 가져오는 어노테이션
//@Controller // --> String
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

    private final FollowService followService;
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(ex.getMessage()));
    }

    //사용자의 팔로우정보 가져오기
    @GetMapping("/followers/{username}")
    public ResponseEntity<List<FollowUserResponseDto>> showFollowers(@PathVariable String username) {
        return ResponseEntity.ok().body(followService.showFollowers(username));
    }

    //사용자의 팔로잉 정보 가져오기
    @GetMapping("/followings/{username}")
    public ResponseEntity<List<FollowUserResponseDto>> showFollowings(@PathVariable String username) {
        return ResponseEntity.ok().body(followService.showFollowings(username));
    }

    //지정 사용자 팔로우 하기
    @PostMapping("/follow/{username}")
    public ResponseEntity<ApiResponseDto> startFollowing(@PathVariable String username, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(followService.startFollowing(username, userDetails.getUser()));
    }

    //지정 사용자 언팔로우 하기
    @DeleteMapping("/follow/{username}")
    public ResponseEntity<ApiResponseDto> unFollowing(@PathVariable String username, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(followService.unFollowing(username, userDetails.getUser()));
    }

}
