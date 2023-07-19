package com.sparta.sns.controller;

import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String goHome(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {

        UserRoleEnum role = userDetails.getUser().getRole();

        if (role == UserRoleEnum.ADMIN) {
            return "admin";
        }
        if (role == UserRoleEnum.DENY) {



            return "deny";
        }
        return "main";
    }

    @GetMapping("/deny")
    public String deny() {
        return "deny";
    }

}
