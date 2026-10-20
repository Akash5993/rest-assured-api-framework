package com.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Reply from POST /auth/login (and /auth/refresh) on the JWT API.
 * The real reply also has id, email, firstName... - we only need the two tokens,
 * so ignoreUnknown tells Jackson to skip the rest instead of failing.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtLoginResponse {

    // Short-lived JWT sent with every protected request.
    private String accessToken;

    // Longer-lived token used ONLY to get a new access token when it expires.
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
