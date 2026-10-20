package com.api.services;

import com.api.client.ApiClient;
import com.api.config.ConfigReader;
import com.api.models.AuthRequest;
import com.api.models.AuthResponse;

import io.restassured.response.Response;

/**
 * Everything to do with the /auth endpoint, in one place.
 * PRINCIPLE: Single Responsibility - this class only knows about authentication.
 * Tests call this class; they never need to know the path or the HTTP method.
 */
public class AuthService {

    // The path is written once, here (DRY). Constants use UPPER_CASE names.
    private static final String AUTH_PATH = "/auth";

    // The INTERFACE type, not RestAssuredApiClient (Dependency Inversion).
    // final: set once in the constructor, never replaced.
    private final ApiClient apiClient;

    // The client is passed IN (dependency injection) - this class never creates it with "new".
    public AuthService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // Sends the login request and returns the server's response.
    // No assertions here: checking the result is the test's job.
    public Response createToken(AuthRequest authRequest) {
        return apiClient.post(AUTH_PATH, authRequest);
    }

    // Logs in with the config credentials and returns just the token.
    // Tests that only NEED a token don't have to know how login works.
    public String getToken() {
        ConfigReader configReader = ConfigReader.getInstance();
        String username = configReader.get("auth.username");
        String password = configReader.get("auth.password");

        AuthRequest authRequest = new AuthRequest(username, password);
        Response response = createToken(authRequest);

        // Raw Response -> our AuthResponse model, which knows about the token.
        AuthResponse authResponse = response.as(AuthResponse.class);
        return authResponse.getToken();
    }
}
