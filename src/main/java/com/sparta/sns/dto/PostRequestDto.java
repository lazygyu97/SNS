package com.sparta.sns.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class PostRequestDto {

    private String content;
    private String imageUrl;

    public PostRequestDto(String content) {
        this.content = content;
    }
}
