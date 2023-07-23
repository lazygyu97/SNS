package com.sparta.sns.service;

import com.sparta.sns.comparator.PostComparator;
import com.sparta.sns.config.FileComponent;
import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.entity.Follow;
import com.sparta.sns.entity.Post;
import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.repository.FollowRepository;
import com.sparta.sns.repository.PostRepository;
import com.sparta.sns.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private FollowRepository followRepository;

    // 이미지 업로드
    private final FileComponent fileComponent;

    // 게시글 작성
    public ApiResponseDto createPost(PostRequestDto requestDto, MultipartFile file, User user) {
        String content = requestDto.getContent();

        if (content.isEmpty()){
            throw new IllegalArgumentException("글을 작성해주세요.");
        }

        // file 비어있지 않으면 imageUrl set
        try {
            if(!file.isEmpty()) {
                String storedFileName = fileComponent.upload(file);
                requestDto.setImageUrl(storedFileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Post 객체 만들기
        Post post = new Post(requestDto,user);

        // Post 객체 DB에 저장
        postRepository.save(post);
        return new ApiResponseDto("글 작성 완료");
    }

    // 게시글 전체 조회
    public List<PostResponseDto> getAllPosts() {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(); // 내림차순 정렬

        List<PostResponseDto> postResponseDto = new ArrayList<>();

        // 게시글 목록 List에 담기
        for (Post post : postList) {
            postResponseDto.add(new PostResponseDto(post));
        }
        return postResponseDto;
    }

    // 게시글 수정
    @Transactional
    public String updatePost(Long id, PostRequestDto requestDto, User user) {
        Post post = confirmUser(id, user);
        post.update(requestDto);

        return "수정 완료";
    }

    // 게시글 삭제
    @Transactional
    public String deletePost(Long id, User user) {
        Post post = confirmUser(id, user);
        postRepository.delete(post);
        return "삭제 완료";
    }

    // 게시글 신고
    @Transactional
    public String reportPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));
        post.report();

        return "게시글 신고 완료";
    }

    //username을 통한 게시글 찾아오기
    public List<PostResponseDto> getPostByUsername(Long id){
        if(postRepository.findByUserIdOrderByCreatedAtDesc(id).isEmpty()){
            throw new IllegalArgumentException("작성한 글이 없습니다.");
        }


        List<PostResponseDto> postResponseDto = new ArrayList<>();

        // 게시글 목록 List에 담기
        for (Post post :  postRepository.findByUserIdOrderByCreatedAtDesc(id)) {
            postResponseDto.add(new PostResponseDto(post));
        }
        return postResponseDto;

    }


    // 게시글 수정, 삭제 시 유저 권한 확인
    private Post confirmUser(Long id, User user){
        // 글 가져오기
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));

        if(user.getRole() == UserRoleEnum.USER){ // 일반 회원
            if(post.getUser().getId() == user.getId()){ // 작성자 검증
                return post;
            }else{
                throw new IllegalArgumentException("작성자가 아닙니다.");
            }
        }else { // 관리자 회원
            return post;
        }
    }


    public List<PostResponseDto> followingUsersPosts(User user) {
        User loginedUser = findUser(user.getUsername());

        //로그인한 유저의 팔로잉 정보 가져오기
        List<Follow> followInfos = followRepository.findByUser(loginedUser);

        //팔로잉 유저의 게시글을 담을 List 생성
        List<Post> followingUsersPosts = new ArrayList<>();
        //팔로잉 유저가 작성한 게시글들을 찾아 저장
        for(Follow followInfo : followInfos){
            List<Post> posts = postRepository.findByUser(followInfo.getFollowingUser());
//            List<Post> posts = postRepository.findByUserAndReport_flag(followInfo.getFollowingUser(),false);
            followingUsersPosts.addAll(posts);
        }
        //최신 날짜 순으로 정렬
        Collections.sort(followingUsersPosts,new PostComparator());

        // 타입변경 stream.map 이용하여 변환 후 반환하기
        return followingUsersPosts.stream().map(PostResponseDto::new).toList();

    }

    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다.")
        );
    }
}
