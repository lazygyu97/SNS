package com.sparta.sns.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.sns.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ProfileResponseDto {

	private String nickname;
	private String username;
	private String email;
	private String image;
	private String oneLine;

	public ProfileResponseDto(User user) {
		this.nickname =user.getNickname();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.image = user.getImage();
		this.oneLine = user.getOneLine();
	}
}
