package com.api.client;

import com.api.config.ConfigReader;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * The real implementation of ApiClient, using Rest Assured.
 * This is the ONLY class in the framework that calls given()/when().
 */
public class RestAssuredApiClient implements ApiClient {

    // Which server this client talks to. Set once, never changed.
    private final String baseUrl;

    // Remembered after login. null until then - nothing is attached.
    private String token;
    private String bearerToken;

    // Default: the booking API from config (existing tests keep working unchanged).
    public RestAssuredApiClient() {
        this(ConfigReader.getInstance().get("base.url"));
    }

    // Any other API, e.g. the JWT API: new RestAssuredApiClient(jwtBaseUrl).
    public RestAssuredApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Response get(String path) {
        return helper().when().get(path);
    }

    @Override
    public Response post(String path, Object body) {
        return helper().body(body).when().post(path);
    }

    @Override
    public Response put(String path, Object body) {
        return helper().body(body).when().put(path);
    }

    @Override
    public Response delete(String path) {
        return helper().when().delete(path);
    }

    // Called once after login; stores the token for all later requests.
    @Override
    public void setToken(String token) {
        this.token = token;
    }

    // Called after a JWT login; stores the access token for all later requests.
    @Override
    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    // Every request starts here, so setup, logging and tokens live in ONE place (DRY).
    private RequestSpecification helper() {
        RequestSpecification request = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                // Print every request and response to the console - helps debug failures.
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());

        // Restful Booker reads its token from a cookie named "token".
        if (token != null) {
            request.cookie("token", token);
        }
        // JWT APIs read the token from the Authorization header, with the word "Bearer".
        if (bearerToken != null) {
            request.header("Authorization", "Bearer " + bearerToken);
        }
        return request;
    }
}
