package com.sparta.sns.controller;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import com.sun.jdi.request.DuplicateRequestException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String createPost(@RequestPart(value = "content") String content,
                             @Nullable @RequestPart(value = "file") MultipartFile image,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 토큰 검사
        checkToken(userDetails);
        PostRequestDto requestDto = new PostRequestDto(content);

        if (image == null) {
            System.out.println("이미지 없음~~~~~~~~~~~~~~~~~₩");
            postService.createPost_2(requestDto, userDetails.getUser());
            return "redirect:/";
        }

        postService.createPost(requestDto, image, userDetails.getUser());
        return "redirect:/";
    }

    // 게시글 조회 (목록 전체)
    @ResponseBody
    @GetMapping("/all-posts")
    public List<PostResponseDto> getAllPosts() {
        return postService.getAllPosts();
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 토큰 검사
        checkToken(userDetails);
        postService.deletePost(id, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("삭제 성공"));
    }

    // 게시글 신고
    @DeleteMapping("/posts/report/{id}")
    public ResponseEntity<ApiResponseDto> reportPost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 토큰 검사
        checkToken(userDetails);
        postService.reportPost(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("신고 성공"));
    }
    // 게시글 신고
    @PutMapping("/posts/report/{id}")
    public ResponseEntity<ApiResponseDto> reportCancelPost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 토큰 검사
        checkToken(userDetails);
        postService.reportCancelPost(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("신고 해제 성공"));
    }

    //7. 글 좋아요 기능
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponseDto> likePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        try {
            postService.likePost(id, userDetails.getUser());
        } catch (DuplicateRequestException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("게시글 좋아요 성공"));
    }

    @PutMapping("/posts/update1/{id}")
    public ResponseEntity<ApiResponseDto> updatePost1(@RequestPart(value = "content") String content,
                                                     @Nullable @RequestPart(value = "file") MultipartFile image,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long id) {
        // 토큰 검사
        checkToken(userDetails);

        postService.updatePost2(id,userDetails.getUser(),content,image);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("글 수정 성공"));
    }
    @PutMapping("/posts/update2/{id}")
    public ResponseEntity<ApiResponseDto> updatePost2(@RequestPart(value = "content") String content,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long id) {
        // 토큰 검사
        checkToken(userDetails);

        postService.updatePost1(id,userDetails.getUser(),content);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseDto("글 수정 성공"));
    }


    // 로그인 토큰 검증
    public void checkToken(UserDetailsImpl userDetails) throws JwtException {
        if (userDetails == null)
            throw new IllegalArgumentException("토큰이 유효하지 않습니다");
    }
}