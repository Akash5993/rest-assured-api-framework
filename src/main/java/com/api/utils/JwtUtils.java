package com.api.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads what is inside a JWT.
 * A JWT is three Base64 parts joined by dots: header.payload.signature
 * The payload is plain JSON (NOT encrypted) - anyone can read it, nobody can change it
 * without breaking the signature.
 */
public final class JwtUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Only static helpers - no objects needed.
    private JwtUtils() {
    }

    // Returns the payload ("claims") as a map, e.g. {"username":"emilys", "exp":1791969656, ...}
    public static Map<String, Object> decodePayload(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Not a JWT: expected 3 parts, got " + parts.length);
        }

        // JWT uses the URL-safe Base64 alphabet.
        byte[] json = Base64.getUrlDecoder().decode(parts[1]);
        try {
            return MAPPER.readValue(new String(json, StandardCharsets.UTF_8), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("JWT payload is not valid JSON", e);
        }
    }
}
