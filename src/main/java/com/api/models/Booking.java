package com.api.models;

/**
 * Body for POST /booking (and the shape returned by GET /booking/{id}).
 * JSON: {"firstname", "lastname", "totalprice", "depositpaid", "bookingdates",
 * "additionalneeds"}
 * PATTERN: Builder - follow BookingDates.java part by part.
 */
public class Booking {

    // TODO 1: Six private fields (NOT final - Jackson fills them when reading a
    // response).
    // firstname, lastname, additionalneeds -> String
    // totalprice -> int
    // depositpaid -> boolean
    // bookingdates -> BookingDates
    // Names must match the JSON keys exactly (all lower case).
    private String firstname, lastname, additionalneeds;
    private int totalprice;
    private boolean depositpaid;
    private BookingDates bookingdates;

    // TODO 2: Private empty constructor (forces use of the Builder; Jackson can
    // still use it).
    private Booking() {
    }
    // TODO 3: Six getters.
    // Rule: "get" + field name with ONLY the first letter capitalised (firstname ->
    // getFirstname).
    // For the boolean use "get" too (getDepositpaid), so the JSON key stays
    // "depositpaid".

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public boolean isDepositpaid() {
        return depositpaid;
    }

    public BookingDates getBookingdates() {
        return bookingdates;
    }

    // TODO 4: public static builder() method that returns a new Builder.

    public static Builder builder() {

        return new Builder();
    }

    // TODO 5: public static nested class Builder.
    // 5a. The same six private fields.
    // 5b. Six methods, one per field: store the value, return this.
    // 5c. build():
    // - FIRST: if firstname or lastname is null -> throw IllegalStateException
    // with a clear message (e.g. which field is missing).
    // - THEN: create a Booking, copy all six values into it, return it.

    public static class Builder {
        private String firstname, lastname, additionalneeds;
        private int totalprice;
        private boolean depositpaid;
        private BookingDates bookingdates;

        public Builder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public Builder additionalneeds(String additionalneeds) {
            this.additionalneeds = additionalneeds;
            return this;
        }

        public Builder totalprice(int totalprice) {
            this.totalprice = totalprice;
            return this;
        }

        public Builder depositpaid(boolean depositpaid) {
            this.depositpaid = depositpaid;
            return this;
        }

        public Builder bookingdates(BookingDates bookingdates) {
            this.bookingdates = bookingdates;
            return this;
        }

        public Booking build() {
            if (firstname == null) {
                throw new IllegalStateException("First name is null");
            }
            if (lastname == null) {
                throw new IllegalStateException("Last name is null");
            }

            Booking booking = new Booking();
            booking.firstname = this.firstname;
            booking.lastname = this.lastname;
            booking.totalprice = totalprice;
            booking.additionalneeds = additionalneeds;
            booking.depositpaid = depositpaid;
            booking.bookingdates = bookingdates;
            return booking;
        }

    }
}
