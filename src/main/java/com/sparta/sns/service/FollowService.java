package com.sparta.sns.service;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.FollowUserResponseDto;
import com.sparta.sns.entity.Follow;
import com.sparta.sns.entity.User;
import com.sparta.sns.repository.FollowRepository;
import com.sparta.sns.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FollowService {

    UserRepository userRepository;
    FollowRepository followRepository;


    //파라미터 username의 팔로워(user가 팔로우하는 사람) 확인
    public List<FollowUserResponseDto> showFollowers(String username) {
        User targetUser = findUser(username);

        List<Follow> followerInfos = followRepository.findByUser(targetUser);
        List<FollowUserResponseDto> followers = new ArrayList<>();
        for (Follow followerInfo : followerInfos) {
            //user가 팔로우 하고 있는 유저정보들을 List타입으로 저장
            followers.add(new FollowUserResponseDto(followerInfo.getFollowingUser(),followerInfo.getFollowingDateTime()));
        }
        return followers;
    }

    // 파라미터 username의 팔로잉(user를 팔로우하는 사람) 확인
    public List<FollowUserResponseDto> showFollowings(String username) {
        User targetUser = findUser(username);
        List<Follow> followingInfos = followRepository.findByFollowingUser(targetUser);
        List<FollowUserResponseDto> followings = new ArrayList<>();
        for(Follow followingInfo : followingInfos) {
            //user를 팔로우 하고 있는 유저정보들을 List타입으로 저장
            followings.add(new FollowUserResponseDto(followingInfo.getUser(), followingInfo.getFollowingDateTime()));
        }
        return followings;
    }


    //팔로우 메서드
    public ApiResponseDto startFollowing(String followingUsername, User user) {
        ///로그인한 유저
        User loginedUser = findUser(user.getUsername());
        //팔로우할 유저
        User followingUser = findUser(followingUsername);

        //이미 팔로우 상태인지 확인
        if(findFollow(loginedUser,followingUser)!=null){
            throw new IllegalArgumentException("이미 팔로우된 사용자 입니다.");
        }
        //팔로우 테이블에 저장
        Follow follow = new Follow(loginedUser,followingUser);
        followRepository.save(follow);
        return new ApiResponseDto(followingUser.getUsername()+"님을 팔로우 했습니다.");
    }
    //팔로우 취소 메서드
    @Transactional
    public ApiResponseDto unFollowing(String followingUsername, User user) {
        ///로그인한 유저
        User loginedUser = findUser(user.getUsername());
        //언팔로우할 유저
        User followingUser = findUser(followingUsername);

        //팔로우 상태가 아닌지 확인
        Follow follow = findFollow(loginedUser, followingUser);
        if (follow ==null) {
            throw new IllegalArgumentException("팔로우한 사용자가 아닙니다. 팔로우 취소할 수 없습니다.");
        }

        followRepository.delete(follow);
        return new ApiResponseDto(followingUser.getUsername()+"님을 팔로우 취소했습니다.");

    }

    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다.")
        );
    }
    //팔로우 상태 여부 확인 (팔로우하기 / 언팔로우하기 위함)
    private Follow findFollow(User user, User followingUser){
        return followRepository.findByUserAndFollowingUser(user,followingUser).orElse(null);
    }
}
