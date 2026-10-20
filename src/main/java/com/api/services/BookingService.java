package com.api.services;

import com.api.client.ApiClient;
import com.api.models.Booking;

import io.restassured.response.Response;

/**
 * Everything to do with the /booking endpoint, in one place.
 * PRINCIPLE: Single Responsibility - one service per API resource.
 */
public class BookingService {

    private static final String BOOKING_PATH = "/booking";

    private final ApiClient apiClient;

    // Same idea as AuthService: the client is received, not created.
    public BookingService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // POST /booking - the Booking object is turned into JSON by Jackson.
    public Response createBooking(Booking booking) {
        return apiClient.post(BOOKING_PATH, booking);
    }

    // GET /booking/{id}, e.g. /booking/4356
    public Response getBooking(int bookingId) {
        return apiClient.get(BOOKING_PATH + "/" + bookingId);
    }

    // GET /booking - returns the list of all booking ids
    public Response getAllBookings() {
        return apiClient.get(BOOKING_PATH);
    }

    // PUT /booking/{id} - replaces the whole booking. Needs the token.
    public Response updateBooking(int bookingId, Booking booking) {
        return apiClient.put(BOOKING_PATH + "/" + bookingId, booking);
    }

    // DELETE /booking/{id} - needs the token.
    public Response deleteBooking(int bookingId) {
        return apiClient.delete(BOOKING_PATH + "/" + bookingId);
    }

    // No token code here! The client attaches it. The service stays simple.
}
