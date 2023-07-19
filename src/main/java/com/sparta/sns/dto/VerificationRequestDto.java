package com.sparta.sns.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerificationRequestDto {
    @Email
    @NotBlank(message = "이메일은 필수로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "인증번호 입력란이 비었습니다.")
    private String authKey;

}