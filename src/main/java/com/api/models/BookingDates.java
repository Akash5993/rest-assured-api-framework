package com.api.models;

/**
 * JSON shape: {"checkin": "2026-01-01", "checkout": "2026-01-05"}
 * PATTERN: Builder - create it with BookingDates.builder().checkin(..).checkout(..).build()
 */
public class BookingDates {

    // Not final: Jackson fills them in AFTER creating the object (when reading a response)
    private String checkin;
    private String checkout;

    // private: outside code must use the Builder. Jackson can still use it (for responses).
    private BookingDates() {
    }

    // Getters: Jackson uses them to write JSON, tests use them to read values
    public String getCheckin() {
        return checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    // Entry point of the chain: BookingDates.builder()
    // static: it's called on the class, because no BookingDates object exists yet
    public static Builder builder() {
        return new Builder();
    }

    // The Builder: collects values step by step, then creates the real object in build()
    // static: a Builder must exist BEFORE any BookingDates object exists
    public static class Builder {

        // Same fields as the outer class - this is where values wait until build()
        private String checkin;
        private String checkout;

        // Each method stores one value and returns the Builder itself (this),
        // so the next method can be chained straight after it
        public Builder checkin(String checkin) {
            this.checkin = checkin;
            return this;
        }

        public Builder checkout(String checkout) {
            this.checkout = checkout;
            return this;
        }

        // Creates the real object and copies the collected values into it.
        // The Builder is nested inside BookingDates, so it may use the private constructor and fields.
        public BookingDates build() {
            BookingDates dates = new BookingDates();
            dates.checkin = this.checkin;
            dates.checkout = this.checkout;
            return dates;
        }
    }
}
