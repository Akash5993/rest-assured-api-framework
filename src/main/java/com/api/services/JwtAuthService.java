package com.api.services;

import com.api.client.ApiClient;
import com.api.config.ConfigReader;
import com.api.models.AuthRequest;
import com.api.models.JwtLoginResponse;
import com.api.models.RefreshTokenRequest;

import io.restassured.response.Response;

/**
 * Everything to do with JWT auth on the DummyJSON API, in one place.
 * Same rules as AuthService: knows the paths, returns Responses, no assertions.
 */
public class JwtAuthService {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String CURRENT_USER_PATH = "/auth/me";
    private static final String REFRESH_PATH = "/auth/refresh";

    private final ApiClient apiClient;

    public JwtAuthService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // POST /auth/login - same username/password body as Restful Booker, so AuthRequest is reused (DRY).
    public Response login(AuthRequest authRequest) {
        return apiClient.post(LOGIN_PATH, authRequest);
    }

    // GET /auth/me - a PROTECTED endpoint: only works with a valid Bearer token.
    public Response getCurrentUser() {
        return apiClient.get(CURRENT_USER_PATH);
    }

    // POST /auth/refresh - swaps a refresh token for a new access token.
    public Response refresh(String refreshToken) {
        return apiClient.post(REFRESH_PATH, new RefreshTokenRequest(refreshToken));
    }

    // Logs in with the config user and returns both tokens.
    public JwtLoginResponse loginAsDefaultUser() {
        ConfigReader config = ConfigReader.getInstance();
        AuthRequest authRequest = new AuthRequest(config.get("jwt.username"), config.get("jwt.password"));
        return login(authRequest).as(JwtLoginResponse.class);
    }
}
