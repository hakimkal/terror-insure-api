package com.terron.security;

public class SecurityConstant {
    public static final String SECRET = "dvsvzrbhresdxctrtsersede442";
    public static final long EXPIRATION_TIME = 432_000_000; // 12 hours
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String REGISTER = "/v1/users/";
    public static final String VERIFY_URL = "/v1/users/confirm";
    public static final String UPDATE = "/v1/users/update";
    public static final String RESET_PASSWORD = "/v1/users/reset-password";

}
