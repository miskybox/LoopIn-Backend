package com.loopinback.loopinback.security.constants;

public class SecurityConstants {
    public static final String SECRET = getSecretKey();
    public static final long EXPIRATION_TIME = 300_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private SecurityConstants() {
    }

    private static String getSecretKey() {
        String secretKey = System.getenv("JWT_SECRET_KEY");
        if (secretKey == null || secretKey.isEmpty()) {

            return "i8Duq+Y15U9FQP6n6fD+2V+wEUa8FdNeHAwdNahdfQ8=";
        }
        return secretKey;
    }
}
