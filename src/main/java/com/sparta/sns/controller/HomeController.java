package com.sparta.sns.controller;

import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.entity.Post;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import lombok.AllArgsConstructor;
import org.hibernate.dialect.sequence.PostgreSQLSequenceSupport;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {

    private PostService postService;

    // 메인 페이지
    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model){
    List<PostResponseDto> posts = postService.getAllPosts();
    model.addAttribute("postlist",posts); // service에서 리턴한 값을 담아서 html로 보내줌

        return "posts";
    }
}
