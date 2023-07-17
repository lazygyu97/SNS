package com.sparta.sns.entity;

public enum UserRoleEnum {
    USER("ROLE_USER"),   // 사용자 권한
    ADMIN("ROLE_ADMIN"),  // 관리자 권한
    DENY("ROLE_DENY");    // 차단된 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }
}