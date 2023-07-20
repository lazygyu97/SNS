package com.sparta.sns.dto;

import com.sparta.sns.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileRequestDto {
    @NotBlank(message = "이름 또는 별명을 입력해주세요.")
    @Size(min=2,max=8,message = "이름(별명)은 최소 2글자에서 최대 8글자로 설정 할 수 있습니다.")
    private String nickname;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String oneLine;


}
