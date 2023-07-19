package com.sparta.sns.service;

import com.sparta.sns.dto.ApiResponseDto;
import com.sparta.sns.entity.*;
import com.sparta.sns.repository.CommentLikeRepository;
import com.sparta.sns.repository.CommentRepository;
import com.sparta.sns.repository.PostLikeRepository;
import com.sparta.sns.repository.PostRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LikeService {
    private CommentRepository commentRepository;
    private PostLikeRepository postLikeRepository;
    private CommentLikeRepository commentLikeRepository;
    private PostRepository postRepository;

    // 게시글 좋아요
    @Transactional
    public ApiResponseDto likePost(Long id, User user) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));

        // 중복 좋아요 방지
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);
        if (postLike.isPresent()){
            throw new DuplicateRequestException("이미 좋아요 한 글 입니다.");
        }

        // postRepository DB저장
        postLikeRepository.save(new PostLike(user, post));
        return new ApiResponseDto("좋아요를 눌렀습니다.");
    }

    // 게시글 좋아요 취소
    @Transactional
    public ApiResponseDto deleteLikePost(Long id, User user) {
        Post post = postRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));

        // 좋아요 한 댓글만 취소 가능
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);
        if (!postLike.isPresent()){
            throw new DuplicateRequestException("좋아요 한 적 없는 글 입니다.");
        }else{ // postRepository DB 삭제
            postLikeRepository.delete(postLike.get());
            return new ApiResponseDto("좋아요를 취소했습니다.");
        }
    }

    // 댓글 좋아요
    public ApiResponseDto likeComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 댓글이 존재하지 않습니다."));

        // 중복 좋아요 방지
        Optional<CommentLike> commentLike = commentLikeRepository.findByUserAndComment(user, comment);
        if (commentLike.isPresent()){
            throw new DuplicateRequestException("이미 좋아요 한 댓글 입니다.");
        }

        // postRepository DB저장
        commentLikeRepository.save(new CommentLike(user, comment));
        return new ApiResponseDto("좋아요를 눌렀습니다.");
    }

    public ApiResponseDto deleteLikeComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 글이 존재하지 않습니다."));

        // 좋아요 한 댓글만 취소 가능
        Optional<CommentLike> commentLike = commentLikeRepository.findByUserAndComment(user, comment);
        if (!commentLike.isPresent()){
            throw new DuplicateRequestException("좋아요 한 적 없는 댓글 입니다.");
        }else{ // postRepository DB 삭제
            commentLikeRepository.delete(commentLike.get());
            return new ApiResponseDto("좋아요를 취소했습니다.");
        }
    }
}