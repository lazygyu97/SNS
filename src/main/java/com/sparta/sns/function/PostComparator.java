package com.sparta.sns.function;

import com.sparta.sns.entity.Post;

import java.util.Comparator;

public class PostComparator implements Comparator<Post> {
    @Override
    public int compare(Post a, Post b){
        //a날짜가 b날짜보다 큰 경우  a > b
        if(a.getCreatedAt().isAfter(b.getCreatedAt()))
            return -1;
            //a < b
        else if(a.getCreatedAt().isBefore(b.getCreatedAt()))
            return 1;

        return 0;
    }
}