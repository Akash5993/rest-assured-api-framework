package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.api.client.ApiClient;
import com.api.client.RestAssuredApiClient;
import com.api.config.ConfigReader;
import com.api.models.AuthRequest;
import com.api.models.AuthResponse;
import com.api.services.AuthService;

import io.restassured.response.Response;

/**
 * Tests for the /auth endpoint: one positive, one negative.
 */
public class AuthTest {

    private AuthService authService;

    @BeforeClass
    public void setup() {
        ApiClient apiClient = new RestAssuredApiClient();
        authService = new AuthService(apiClient);
    }

    // Positive: correct username and password -> we get a token.
    @Test
    public void validLoginTest() {
        ConfigReader config = ConfigReader.getInstance();
        AuthRequest request = new AuthRequest(config.get("auth.username"), config.get("auth.password"));

        Response response = authService.createToken(request);
        Assert.assertEquals(response.statusCode(), 200);

        String token = response.as(AuthResponse.class).getToken();
        Assert.assertNotNull(token, "Token should be returned");
        Assert.assertFalse(token.isBlank(), "Token should not be empty");
    }

    // Negative: wrong password -> no token.
    @Test
    public void invalidLoginTest() {
        AuthRequest request = new AuthRequest("admin", "wrongpassword");

        Response response = authService.createToken(request);

        // This API still returns 200 for a bad login (a quirk - most APIs return 401),
        // so we must check the BODY, not just the status code.
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("reason"), "Bad credentials");
        Assert.assertNull(response.jsonPath().getString("token"), "No token for a bad login");
    }
}
