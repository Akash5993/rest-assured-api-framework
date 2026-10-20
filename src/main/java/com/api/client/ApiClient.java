package com.api.client;

import io.restassured.response.Response;

/**
 * The contract for sending HTTP requests.
 * PRINCIPLE: Dependency Inversion - the rest of the framework uses THIS
 * interface,
 * never Rest Assured directly.
 */
public interface ApiClient {

    Response get(String path);

    Response post(String path, Object body);

    Response put(String path, Object body);

    Response delete(String path);

    // Cookie token (Restful Booker): sent as a cookie named "token".
    void setToken(String token);

    // JWT (DummyJSON): sent as the header "Authorization: Bearer <token>".
    void setBearerToken(String bearerToken);
}
