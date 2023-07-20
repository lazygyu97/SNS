package com.sparta.sns.controller;

import com.sparta.sns.dto.DailyPostCountDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.dto.UserGraphResponseDto;
import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.security.UserDetailsImpl;
import com.sparta.sns.service.PostService;
import com.sparta.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping("/")
    public String goHome(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {

        UserRoleEnum role = userDetails.getUser().getRole();
        List<PostResponseDto> postList = postService.getAllPosts();
        List<User> userList = userService.getAllUsers();

        String nickname = userDetails.getUsername();

        if (role == UserRoleEnum.ADMIN) {

            //그래프 data
            List<DailyPostCountDto> dailyPostCountList = getDailyPostCount(postList);
            List<UserGraphResponseDto> dailyUserCountList =getDailyUserCount(userList);

            model.addAttribute("dailyPostCountList", dailyPostCountList);
            model.addAttribute("dailyUserCountList", dailyUserCountList);

            //사용자 및 게시글 관리
            model.addAttribute("postList",postList);
            model.addAttribute("userList",userList);


            return "admin";
        }
        if (role == UserRoleEnum.DENY) {


            return "deny";
        }


        model.addAttribute("list",postList);
        model.addAttribute("nickname",nickname);
        return "main";
    }



    @GetMapping("/deny")
    public String deny() {
        return "deny";
    }


    private List<DailyPostCountDto> getDailyPostCount(List<PostResponseDto> postList) {
        Map<LocalDate, Integer> countMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        for (PostResponseDto post : postList) {

            LocalDateTime createdAt = post.getCreatedAt();
            LocalDate postDate = createdAt.toLocalDate();

            if (postDate.isAfter(sevenDaysAgo) && postDate.isBefore(today.plusDays(1))) {
                countMap.put(postDate, countMap.getOrDefault(postDate, 0) + 1);
            }
        }

        List<DailyPostCountDto> dailyPostCountList = new ArrayList<>();

        for (LocalDate date = sevenDaysAgo; date.isBefore(today.plusDays(1)); date = date.plusDays(1)) {
            int count = countMap.getOrDefault(date, 0);
            DailyPostCountDto dailyPostCountDto = new DailyPostCountDto(date.format(formatter), count);
            dailyPostCountList.add(dailyPostCountDto);
        }

        // 첫날의 작성된 글 수가 0이면 dailyPostCountList에서 제거
        if (dailyPostCountList.size() > 0 && dailyPostCountList.get(0).getPostCount() == 0) {
            dailyPostCountList.remove(0);
        }

        return dailyPostCountList;
    }

    private List<UserGraphResponseDto> getDailyUserCount(List<User> allUsers) {
        Map<LocalDate, Integer> countMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        for (User user : allUsers) {

            LocalDateTime createdAt = user.getCreatedAt();
            LocalDate postDate = createdAt.toLocalDate();

            if (postDate.isAfter(sevenDaysAgo) && postDate.isBefore(today.plusDays(1))) {
                countMap.put(postDate, countMap.getOrDefault(postDate, 0) + 1);
            }
        }

        List<UserGraphResponseDto> dailyPostCountList = new ArrayList<>();

        for (LocalDate date = sevenDaysAgo; date.isBefore(today.plusDays(1)); date = date.plusDays(1)) {
            int count = countMap.getOrDefault(date, 0);
            UserGraphResponseDto userResponseDto = new UserGraphResponseDto(date.format(formatter), count);
            dailyPostCountList.add(userResponseDto);
        }

        // 첫날의 작성된 글 수가 0이면 dailyPostCountList에서 제거
        if (dailyPostCountList.size() > 0 && dailyPostCountList.get(0).getPostCount() == 0) {
            dailyPostCountList.remove(0);
        }

        return dailyPostCountList;

    }

}
