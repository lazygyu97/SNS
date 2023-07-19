package com.sparta.sns.controller;

import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.Commentservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j // log 기능 가져오는 어노테이션
//@Controller // --> String
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private Commentservice commentservice;

    // 프론트 구현 안 해서 paramater 값으로 우선 post id 받아서 구현!
    // 댓글 작성
    @ResponseBody
    @PostMapping("/comments/{postId}")
    public String createComment(@PathVariable Long postId, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentservice.createComment(postId, requestDto,userDetails.getUser());
    }

    // 댓글 삭제
    @ResponseBody
    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentservice.deleteComment(commentId,userDetails.getUser());
    }

    // 댓글 수정
    @ResponseBody
    @PutMapping("/comments/{commentId}")
    public String updateComment(@PathVariable Long commentId, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentservice.updateComment(commentId,requestDto,userDetails.getUser());
    }

    // 댓글 목록 조회 >> 필요한가요...!? 게시글 별로 댓글만 보이면 될 거 같은데..
}