package com.sparta.sns.service;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.dto.PostResponseDto;
import com.sparta.sns.entity.Post;
import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;

    // 게시글 작성
    public ApiResponseDto createPost(PostRequestDto requestDto, User user) {

        String content = requestDto.getContent();

        if (content.isEmpty()){
            throw new IllegalArgumentException("글을 작성해주세요.");
        }

        // Post 객체 만들기
        Post post = new Post(content,user);

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

    //

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
    public String reportPost(Long id, User user) {
        return "게시글 신고 완료";
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
}
