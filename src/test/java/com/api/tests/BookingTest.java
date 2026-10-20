package com.api.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.api.client.ApiClient;
import com.api.client.RestAssuredApiClient;
import com.api.models.Booking;
import com.api.models.BookingDates;
import com.api.models.BookingResponse;
import com.api.services.AuthService;
import com.api.services.BookingService;

import io.restassured.response.Response;

/**
 * Tests for the /booking endpoint.
 * The test only talks to BookingService - it never knows paths, HTTP methods or Rest Assured.
 * Rule: services SEND requests, tests CHECK results (assertions live only here).
 */
public class BookingTest {

    private BookingService bookingService;

    // Runs once, before any test in this class.
    @BeforeClass
    public void setup() {
        // Declared as the interface type (Dependency Inversion).
        ApiClient apiClient = new RestAssuredApiClient();
        bookingService = new BookingService(apiClient);

        // Log in once; from now on every request from this client carries the token.
        // Same apiClient for both services, so they share the token.
        AuthService authService = new AuthService(apiClient);
        apiClient.setToken(authService.getToken());
    }

    @Test
    public void createBookingTest() {
        Booking booking = buildBooking();

        Response response = bookingService.createBooking(booking);
        Assert.assertEquals(response.statusCode(), 200);

        // Jackson turns the JSON body back into our model.
        BookingResponse body = response.as(BookingResponse.class);
        Assert.assertTrue(body.getBookingid() > 0, "Booking id should be positive");
        Assert.assertEquals(body.getBooking().getFirstname(), booking.getFirstname());
    }

    @Test
    public void getBookingTest() {
        // Each test creates its own data, so it never depends on another test.
        Booking booking = buildBooking();
        int bookingId = bookingService.createBooking(booking)
                .as(BookingResponse.class)
                .getBookingid();

        Response response = bookingService.getBooking(bookingId);
        Assert.assertEquals(response.statusCode(), 200);

        // GET returns a plain Booking, not a BookingResponse.
        Booking fetched = response.as(Booking.class);
        Assert.assertEquals(fetched.getFirstname(), booking.getFirstname());
    }

    @Test
    public void getAllBookingsTest() {
        Response response = bookingService.getAllBookings();
        Assert.assertEquals(response.statusCode(), 200);

        // "$" means the root of the JSON, which here is a list.
        List<Object> bookings = response.jsonPath().getList("$");
        Assert.assertFalse(bookings.isEmpty(), "Booking list should not be empty");
    }

    @Test
    public void updateBookingTest() {
        int bookingId = createAndGetId();

        // Same booking, new first name.
        Booking updated = buildBooking("Rahul");
        Response response = bookingService.updateBooking(bookingId, updated);
        Assert.assertEquals(response.statusCode(), 200);

        // PUT returns the updated booking.
        Booking body = response.as(Booking.class);
        Assert.assertEquals(body.getFirstname(), "Rahul");
    }

    @Test
    public void deleteBookingTest() {
        int bookingId = createAndGetId();

        // This API returns 201 on delete (a quirk - most APIs return 200 or 204).
        Response response = bookingService.deleteBooking(bookingId);
        Assert.assertEquals(response.statusCode(), 201);

        // Prove it's really gone.
        Assert.assertEquals(bookingService.getBooking(bookingId).statusCode(), 404);
    }

    // Negative: a client that never logged in has no token -> update is refused.
    @Test
    public void updateWithoutTokenTest() {
        int bookingId = createAndGetId();

        BookingService noTokenService = new BookingService(new RestAssuredApiClient());
        Response response = noTokenService.updateBooking(bookingId, buildBooking("Rahul"));

        // 403 Forbidden: the server knows the request but won't allow it without a token.
        Assert.assertEquals(response.statusCode(), 403);
    }

    // Schema check: the response has the right SHAPE (fields, types), whatever the values.
    @Test
    public void getBookingSchemaTest() {
        int bookingId = createAndGetId();

        Response response = bookingService.getBooking(bookingId);
        response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }

    // Creates a fresh booking and returns its id (used by update and delete).
    private int createAndGetId() {
        return bookingService.createBooking(buildBooking())
                .as(BookingResponse.class)
                .getBookingid();
    }

    // Default test data - most tests don't care about the name.
    private Booking buildBooking() {
        return buildBooking("Akash");
    }

    // Test data in one place (DRY) - only the first name can change.
    private Booking buildBooking(String firstname) {
        BookingDates dates = BookingDates.builder()
                .checkin("2026-11-01")
                .checkout("2026-11-05")
                .build();

        return Booking.builder()
                .firstname(firstname)
                .lastname("Thakur")
                .totalprice(150)
                .depositpaid(true)
                .bookingdates(dates)
                .additionalneeds("Breakfast")
                .build();
    }
}
