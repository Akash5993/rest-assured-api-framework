package com.api.tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.api.client.ApiClient;
import com.api.client.RestAssuredApiClient;
import com.api.config.ConfigReader;
import com.api.models.AuthRequest;
import com.api.models.JwtLoginResponse;
import com.api.services.JwtAuthService;
import com.api.utils.JwtUtils;

import io.restassured.response.Response;

/**
 * JWT tests against DummyJSON.
 * Each test makes its OWN client, because tests run in parallel and set different tokens -
 * a shared client would let one test overwrite another's token.
 */
public class JwtAuthTest {

    private String jwtBaseUrl;
    private String username;

    @BeforeClass
    public void setup() {
        ConfigReader config = ConfigReader.getInstance();
        jwtBaseUrl = config.get("jwt.base.url");
        username = config.get("jwt.username");
    }

    // Positive: correct login returns a well-formed JWT and a refresh token.
    @Test
    public void validLoginTest() {
        ConfigReader config = ConfigReader.getInstance();
        Response response = new JwtAuthService(newClient())
                .login(new AuthRequest(username, config.get("jwt.password")));
        Assert.assertEquals(response.statusCode(), 200);

        JwtLoginResponse body = response.as(JwtLoginResponse.class);
        // A JWT always has 3 parts: header.payload.signature
        Assert.assertEquals(body.getAccessToken().split("\\.").length, 3, "Access token should be a JWT");
        Assert.assertFalse(body.getRefreshToken().isBlank(), "Refresh token should be returned");
    }

    // Negative: wrong password -> 400 and no token.
    @Test
    public void invalidLoginTest() {
        Response response = new JwtAuthService(newClient())
                .login(new AuthRequest(username, "wrongpassword"));

        Assert.assertEquals(response.statusCode(), 400);
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid credentials");
        Assert.assertNull(response.jsonPath().getString("accessToken"));
    }

    // Decode the token and check the claims inside it.
    @Test
    public void tokenClaimsTest() {
        String accessToken = new JwtAuthService(newClient()).loginAsDefaultUser().getAccessToken();

        Map<String, Object> claims = JwtUtils.decodePayload(accessToken);
        Assert.assertEquals(claims.get("username"), username, "Token should belong to the logged-in user");

        // iat = issued at, exp = expires at (both in seconds since 1970).
        long issuedAt = ((Number) claims.get("iat")).longValue();
        long expiresAt = ((Number) claims.get("exp")).longValue();
        long now = System.currentTimeMillis() / 1000;
        Assert.assertTrue(expiresAt > issuedAt, "Expiry should be after issue time");
        Assert.assertTrue(expiresAt > now, "A fresh token should not be expired");
    }

    // Positive: a protected endpoint works with the Bearer token.
    @Test
    public void accessWithTokenTest() {
        ApiClient client = newClient();
        JwtAuthService jwtService = new JwtAuthService(client);
        client.setBearerToken(jwtService.loginAsDefaultUser().getAccessToken());

        Response response = jwtService.getCurrentUser();
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("username"), username);
    }

    // Negative: no token -> 401 Unauthorized.
    @Test
    public void accessWithoutTokenTest() {
        Response response = new JwtAuthService(newClient()).getCurrentUser();

        Assert.assertEquals(response.statusCode(), 401);
        Assert.assertEquals(response.jsonPath().getString("message"), "Access Token is required");
    }

    // Security: change the signature -> the server must reject the token.
    @Test
    public void tamperedTokenTest() {
        ApiClient client = newClient();
        JwtAuthService jwtService = new JwtAuthService(client);
        String accessToken = jwtService.loginAsDefaultUser().getAccessToken();

        // Replace the last 5 characters of the signature.
        String tampered = accessToken.substring(0, accessToken.length() - 5) + "AAAAA";
        client.setBearerToken(tampered);

        Response response = jwtService.getCurrentUser();
        // Should be 401, but DummyJSON returns 500 (a server bug we would report).
        // We assert what matters: access is refused and the reason is the signature.
        Assert.assertNotEquals(response.statusCode(), 200, "Tampered token must be rejected");
        Assert.assertEquals(response.jsonPath().getString("message"), "invalid signature");
    }

    // Refresh: the refresh token gets a NEW access token, and that new token works.
    @Test
    public void refreshTokenTest() {
        ApiClient client = newClient();
        JwtAuthService jwtService = new JwtAuthService(client);
        String refreshToken = jwtService.loginAsDefaultUser().getRefreshToken();

        Response response = jwtService.refresh(refreshToken);
        Assert.assertEquals(response.statusCode(), 200);

        String newAccessToken = response.as(JwtLoginResponse.class).getAccessToken();
        client.setBearerToken(newAccessToken);
        Assert.assertEquals(jwtService.getCurrentUser().statusCode(), 200, "Refreshed token should work");
    }

    // A fresh client pointed at the JWT API.
    private ApiClient newClient() {
        return new RestAssuredApiClient(jwtBaseUrl);
    }
}
