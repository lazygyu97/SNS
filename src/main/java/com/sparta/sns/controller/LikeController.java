package com.sparta.sns.controller;

import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private LikeService likeService;
    @PostMapping("/{postid}/like")
    public void likePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
         likeService.likePost(id, userDetails.getUser());
    }

    @DeleteMapping("/{postid}/like")
    public void deleteLikePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
         likeService.deleteLikePost(id, userDetails.getUser());
    }

    @PostMapping("/comments/{commentsid}/like")
    public void likeComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
         likeService.likeComment(id, userDetails.getUser());
    }

    @DeleteMapping("/comments/{commentsid}/like")
    public void deleteLikeComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
         likeService.deleteLikeComment(id, userDetails.getUser());
    }
}
