package com.flamingo.qa.api.tests.booking.api;

import com.flamingo.qa.api.base.BaseApiTest;
import com.flamingo.qa.api.model.auth.AuthRequest;
import com.flamingo.qa.api.model.auth.AuthResponse;
import com.flamingo.qa.api.model.booking.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingNegativeTest extends BaseApiTest {

    @Test
    @DisplayName("Authentication fails with invalid credentials")
    void shouldFailAuthenticationWithInvalidCredentials() {
        Response response = bookingApiClient.authenticateRaw(new AuthRequest(apiProperties.getUsername(), "wrongpassword"));

        assertThat(response.statusCode()).isEqualTo(200);

        AuthResponse authResponse = response.as(AuthResponse.class);
        assertThat(authResponse.getToken()).isNull();
        assertThat(authResponse.getReason()).isEqualTo("Bad credentials");
    }

    @Test
    @DisplayName("Retrieving a non-existent booking returns 404")
    void shouldReturnNotFoundForNonExistentBooking() {
        bookingApiClient.getBooking(-1)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Updating a booking with an invalid token is forbidden")
    void shouldRejectUpdateWithInvalidToken() {
        Booking originalBooking = BookingTestData.sampleBooking();
        bookingId = bookingApiClient.createBookingAndGetId(originalBooking);

        bookingApiClient.updateBooking(bookingId, BookingTestData.updatedBooking(), "invalid-token")
                .then()
                .statusCode(403);

        assertBookingUnchanged(bookingId, originalBooking);
    }

    @Test
    @DisplayName("Deleting a booking with an invalid token is forbidden")
    void shouldRejectDeleteWithInvalidToken() {
        Booking originalBooking = BookingTestData.sampleBooking();
        bookingId = bookingApiClient.createBookingAndGetId(originalBooking);

        bookingApiClient.deleteBooking(bookingId, "invalid-token")
                .then()
                .statusCode(403);

        assertBookingUnchanged(bookingId, originalBooking);
    }
}
