package com.sparta.sns.controller;

import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private PostService postService;

    // 게시글 작성 - 로그인, 작성 내용 필요
    @ResponseBody
    @PostMapping("/posts")
    public String createPost(@RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 토큰 검사
        checkToken(userDetails);
        return postService.createPost(requestDto,userDetails.getUser());
    }

    // 게시글 조회 (목록 전체)
    @ResponseBody
    @GetMapping("/posts/all")
    public List<PostResponseDto> getAllPosts(){
        return postService.getAllPosts();
    }

    // 게시글 조회 (팔로잉 목록) > 팔로잉 구현 이후 구현!

    // 게시글 수정
    @ResponseBody
    @PutMapping("/posts/{postid}")
    public String updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    // 게시글 삭제
    @ResponseBody
    @DeleteMapping("//posts/{postid")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.deletePost(id, userDetails.getUser());
    }

    // 로그인 토큰 검증
    public void checkToken(UserDetailsImpl userDetails) throws JwtException {
        if(userDetails == null)
            throw new IllegalArgumentException("토큰이 유효하지 않습니다");
    }
}