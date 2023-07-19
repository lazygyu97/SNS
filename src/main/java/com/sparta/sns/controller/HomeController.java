package com.sparta.sns.controller;

// public class HomeController {

// //     private PostService postService;

// //     // 메인 페이지
// //     @GetMapping("/")
// //     public String home(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model){
// //     List<PostResponseDto> posts = postService.getAllPosts();
// //     model.addAttribute("postlist",posts); // service에서 리턴한 값을 담아서 html로 보내줌

// //         return "posts";
// //     }
// }

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "main";
    }
}
