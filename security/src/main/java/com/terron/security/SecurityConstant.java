package com.terron.security;

public class SecurityConstant {
    public static final String SECRET = "dvsvzrbhresdxctrtsersede442";
    public static final long EXPIRATION_TIME = 432_000_000; // 12 hours
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String REGISTER = "/user/register";
    public static final String VERIFY_URL = "/user/confirm";
    public static final String UPDATE = "/user/update";
    public static final String RESET_PASSWORD = "/user/reset-password";

}
