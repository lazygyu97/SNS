package com.sparta.sns.controller;

import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.dto.ProfileRequestDto;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 작성 - 로그인, 이미지, 작성 내용 필요
    @PostMapping("/posts")
    public String createPost(@RequestPart (value = "content") String content,
                             @RequestPart(value = "file") MultipartFile image,
                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 토큰 검사
        checkToken(userDetails);

        PostRequestDto requestDto = new PostRequestDto(content);

        postService.createPost(requestDto,image,userDetails.getUser());
        return "redirect:/";
    }

    // 게시글 조회 (목록 전체)
    @ResponseBody
    @GetMapping("/all-posts")
    public List<PostResponseDto> getAllPosts(){

        System.out.println("controller");
        return postService.getAllPosts();
    }

    // 게시글 조회 (사용자가 팔로잉한 유저의 게시글만 조회)
    @GetMapping("/posts/followingUsers")
    @ResponseBody
    public List<PostResponseDto> followingUsersPosts(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.followingUsersPosts(userDetails.getUser());
    }

    // 게시글 조회 (팔로잉 목록) > 팔로잉 구현 이후 구현!/ 게시글 수정
    @PutMapping("/posts/{postid}")
    public String updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 토큰 검사

        System.out.println("controller");
        checkToken(userDetails);
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postid}")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 토큰 검사

        System.out.println("controller");
        checkToken(userDetails);
        return postService.deletePost(id, userDetails.getUser());
    }

    // 게시글 신고
    @DeleteMapping("/posts/report/{postid}")
    public String reportPost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 토큰 검사

        System.out.println("controller");
        checkToken(userDetails);
        return postService.reportPost(id);
    }


    // 로그인 토큰 검증
    public void checkToken(UserDetailsImpl userDetails) throws JwtException {
        if(userDetails == null)
            throw new IllegalArgumentException("토큰이 유효하지 않습니다");
    }
}