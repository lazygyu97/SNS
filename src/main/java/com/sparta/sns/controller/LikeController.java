package com.sparta.sns.controller;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private LikeService likeService;

    //예외처리 메서드
    //컨트롤러 내 API가 호출되다가 Exception 발생 시, 코드 실행
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto> handleException(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(ex.getMessage()));
    }
    @PostMapping("/{postid}/like")
    public ResponseEntity<ApiResponseDto> likePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.status(200).body(likeService.likePost(id, userDetails.getUser()));
    }

    @DeleteMapping("/{postid}/like")
    public ResponseEntity<ApiResponseDto> deleteLikePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.status(200).body(likeService.deleteLikePost(id, userDetails.getUser()));
    }

    @PostMapping("/comments/{commentsid}/like")
    public ResponseEntity<ApiResponseDto> likeComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.status(200).body(likeService.likeComment(id, userDetails.getUser()));
    }

    @DeleteMapping("/comments/{commentsid}/like")
    public ResponseEntity<ApiResponseDto> deleteLikeComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.status(200).body(likeService.deleteLikeComment(id, userDetails.getUser()));
    }
}
