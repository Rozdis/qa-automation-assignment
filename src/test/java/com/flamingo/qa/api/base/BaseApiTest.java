package com.flamingo.qa.api.base;

import com.flamingo.qa.api.client.BookingApiClient;
import com.flamingo.qa.api.model.auth.AuthRequest;
import com.flamingo.qa.api.model.booking.Booking;
import com.flamingo.qa.base.SpringContext;
import com.flamingo.qa.config.ApiProperties;
import org.junit.jupiter.api.AfterEach;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseApiTest extends BaseRestAssuredTest {

    protected static final ApiProperties apiProperties = SpringContext.INSTANCE.getBean(ApiProperties.class);
    protected static final BookingApiClient bookingApiClient = new BookingApiClient(apiProperties.getBaseUrl());

    protected Integer bookingId;

    @AfterEach
    void cleanUp() {
        if (bookingId != null) {
            bookingApiClient.deleteBooking(bookingId, authenticate());
        }
    }

    protected String authenticate() {
        return bookingApiClient.authenticate(new AuthRequest(apiProperties.getUsername(), apiProperties.getPassword()));
    }

    protected Booking getBookingAndAssertOk(int bookingId) {
        return bookingApiClient.getBooking(bookingId)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    protected void assertBookingUnchanged(int bookingId, Booking expected) {
        assertThat(getBookingAndAssertOk(bookingId)).isEqualTo(expected);
    }
}
