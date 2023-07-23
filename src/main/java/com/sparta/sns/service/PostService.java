package com.sparta.sns.service;

import com.sparta.sns.config.FileComponent;
import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.entity.Post;
import com.sparta.sns.entity.PostLike;
import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.repository.PostLikeRepository;
import com.sparta.sns.repository.PostRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileComponent fileComponent;    // 이미지 업로드

    // 게시글 작성
    public ApiResponseDto createPost(PostRequestDto requestDto, MultipartFile file, User user) {
        String content = requestDto.getContent();

        if (content.isEmpty()) {
            throw new IllegalArgumentException("글을 작성해주세요.");
        }

        // file 비어있지 않으면 imageUrl set
        try {
            if (!file.isEmpty()) {
                String storedFileName = fileComponent.upload(file);
                requestDto.setImageUrl(storedFileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("PostService.createPost :::::::" + requestDto.getImageUrl());
        // Post 객체 만들기
        Post post = new Post(requestDto, user);

        // Post 객체 DB에 저장
        postRepository.save(post);
        return new ApiResponseDto("글 작성 완료");
    }

    //사진이 없는 글 작성
    public ApiResponseDto createPost_2(PostRequestDto requestDto, User user) {
        String content = requestDto.getContent();

        if (content.isEmpty()) {
            throw new IllegalArgumentException("글을 작성해주세요.");
        }

        // Post 객체 만들기
        Post post = new Post(requestDto, user);

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

    //신고된 글 가져오기
    public List<PostResponseDto> getReportPosts() {
        List<Post> postList =postRepository.findByReportOrderByCreatedAtDesc(true);

        List<PostResponseDto> postResponseDto = new ArrayList<>();

        // 게시글 목록 List에 담기
        for (Post post : postList) {
            postResponseDto.add(new PostResponseDto(post));
        }
        return postResponseDto;
    }

    //신고가 안된 정상적인 글 가져오기
    public List<PostResponseDto> getNotReportPostList() {
        List<Post> postList =postRepository.findByReportOrderByCreatedAtDesc(false);

        List<PostResponseDto> postResponseDto = new ArrayList<>();

        // 게시글 목록 List에 담기
        for (Post post : postList) {
            postResponseDto.add(new PostResponseDto(post));
        }
        return postResponseDto;
    }

    //post_id를 통한 게시글 찾아오기
    public List<PostResponseDto> getPostById(Long id) {
        if (!postRepository.findByUserIdOrderByCreatedAtDesc(id).isEmpty()) {
            List<PostResponseDto> postResponseDto = new ArrayList<>();

            // 게시글 목록 List에 담기
            for (Post post : postRepository.findByUserIdOrderByCreatedAtDesc(id)) {
                postResponseDto.add(new PostResponseDto(post));
            }
            return postResponseDto;
        }
        List<PostResponseDto> postResponseDto = new ArrayList<>();

        return postResponseDto;

    }


    // username으로 게시글 가져오기
    public List<PostResponseDto> getPostByUsername(String username) {
        if (!postRepository.findByUsernameOrderByCreatedAtDesc(username).isEmpty()) {
            List<PostResponseDto> postResponseDto = new ArrayList<>();

            // 게시글 목록 List에 담기
            for (Post post : postRepository.findByUsernameOrderByCreatedAtDesc(username)) {
                postResponseDto.add(new PostResponseDto(post));
            }
            return postResponseDto;
        }
        List<PostResponseDto> postResponseDto = new ArrayList<>();

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
    public void reportPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));
        post.report();
    }

    // 게시글 신고
    @Transactional
    public void reportCancelPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));
        post.cancelReport();
    }


    //글 좋아요 기능
    @Transactional
    public void likePost(Long id, User user) {
        Post post = findPost(id);
        Optional<PostLike> postLikeOptional = checkLike(user, post);

        if (postLikeOptional.isPresent()) {
            postLikeRepository.delete(postLikeOptional.get());
        } else {
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);
        }
    }


    // 게시글 수정, 삭제 시 유저 권한 확인 메서드
    private Post confirmUser(Long id, User user) {
        // 글 가져오기
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));

        if (user.getRole() == UserRoleEnum.USER) { // 일반 회원
            if (post.getUser().getId() == user.getId()) { // 작성자 검증
                return post;
            } else {
                throw new IllegalArgumentException("작성자가 아닙니다.");
            }
        } else { // 관리자 회원
            return post;
        }
    }

    //글 좋아요 확인 메서드
    public Optional<PostLike> checkLike(User user, Post post) {
        return postLikeRepository.findByUserAndPost(user, post);
    }

    //글이 있는지 확인하는 메서드
    public Post findPost(long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("선택한 게시글은 존재하지 않습니다."));
    }


}
