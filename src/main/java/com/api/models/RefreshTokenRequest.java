package com.api.models;

/**
 * Body for POST /auth/refresh: {"refreshToken": "..."}
 */
public class RefreshTokenRequest {

    private final String refreshToken;

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
