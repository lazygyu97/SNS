package com.sparta.sns.service;

import com.sparta.sns.dto.CommentRequestDto;
import com.sparta.sns.dto.PostRequestDto;
import com.sparta.sns.entity.Comment;
import com.sparta.sns.entity.Post;
import com.sparta.sns.entity.User;
import com.sparta.sns.entity.UserRoleEnum;
import com.sparta.sns.repository.CommentRepository;
import com.sparta.sns.repository.PostRepository;
import com.sparta.sns.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class Commentservice {
    private CommentRepository commentRepository;
    private PostRepository postRepository;

    // 댓글 작성
    public String createComment(CommentRequestDto requestDto, User user) {
        //게시글의 DB 저장 유무 확인 및 가져오기
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(() ->
                        new IllegalArgumentException("선택한 글은 존재하지 않습니다."));

        // entity 생성
        Comment comment = new Comment(requestDto, user, post);

        // DB 저장
        commentRepository.save(comment);
        return "게시글 작성 완료";
    }

    public String deleteComment(Long commentId, User user) {

        //게시글의 DB 저장 유무 확인 및 유저 확인 후 가져오기
        Comment comment = confirmUser(commentId, user);

        // DB에서 삭제
        commentRepository.delete(comment);

        return "댓글 삭제 완료";
    }

    // 댓글 수정
    @Transactional
    public String updateComment(Long commentId, PostRequestDto requestDto, User user) {

        // 댓글의 DB 저장 유무 확인 및 유저 확인 후 가져오기
        Comment comment = confirmUser(commentId,user);

        // entity 업데이트
        comment.update(requestDto);
        return "댓글 수정 완료";
    }

    // 댓글 수정, 삭제 시 유저 권한 확인
    private Comment confirmUser(Long id, User user){
        // 글 가져오기
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택하신 댓글이 존재하지 않습니다."));

        if(user.getRole() == UserRoleEnum.USER){ // 일반 회원
            if(comment.getUser().getId() == user.getId()){ // 작성자 검증
                return comment;
            }else{
                throw new IllegalArgumentException("작성자가 아닙니다.");
            }
        }else { // 관리자 회원
            return comment;
        }
    }
}