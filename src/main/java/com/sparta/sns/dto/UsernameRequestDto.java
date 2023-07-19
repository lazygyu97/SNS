package com.sparta.sns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,16}$", message = "아이디는 3~16자의 영문 대소문자, 숫자, 특수문자(_,-)만 사용할 수 있습니다.")
    private String username;
}
