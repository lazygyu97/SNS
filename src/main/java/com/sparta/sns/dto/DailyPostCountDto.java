package com.sparta.sns.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyPostCountDto {
    private String date;
    private int postCount;

    public DailyPostCountDto(String date, int postCount) {
        this.date = date;
        this.postCount = postCount;
    }

}
