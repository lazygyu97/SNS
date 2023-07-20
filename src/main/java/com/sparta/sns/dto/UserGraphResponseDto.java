package com.sparta.sns.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGraphResponseDto {
    private String date;
    private int postCount;

    public UserGraphResponseDto(String date, int postCount) {
        this.date = date;
        this.postCount = postCount;
    }
}
