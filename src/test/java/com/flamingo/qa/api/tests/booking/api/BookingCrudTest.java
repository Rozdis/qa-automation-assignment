package com.flamingo.qa.api.tests.booking.api;

import com.flamingo.qa.api.base.BaseApiTest;
import com.flamingo.qa.api.model.booking.Booking;
import com.flamingo.qa.api.model.booking.CreateBookingResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class BookingCrudTest extends BaseApiTest {

    @Test
    @DisplayName("Authenticate and receive a valid token")
    void shouldAuthenticateAndReturnToken() {
        assertThat(authenticate()).isNotBlank();
    }

    @ParameterizedTest(name = "Create a new booking for {0}")
    @MethodSource("com.flamingo.qa.api.tests.booking.api.BookingTestData#bookingProvider")
    @DisplayName("Create a new booking")
    void shouldCreateBooking(Booking booking) {
        Response response = bookingApiClient.createBooking(booking);

        assertThat(response.statusCode()).isEqualTo(200);

        CreateBookingResponse createBookingResponse = response.as(CreateBookingResponse.class);
        bookingId = createBookingResponse.getBookingid();

        assertThat(bookingId).isPositive();
        assertThat(createBookingResponse.getBooking()).isEqualTo(booking);
    }

    @Test
    @DisplayName("Retrieve a booking by id")
    void shouldRetrieveBookingById() {
        Booking booking = BookingTestData.sampleBooking();
        bookingId = bookingApiClient.createBookingAndGetId(booking);

        assertBookingUnchanged(bookingId, booking);
    }

    @Test
    @DisplayName("Update a booking with new details")
    void shouldUpdateBooking() {
        bookingId = bookingApiClient.createBookingAndGetId(BookingTestData.sampleBooking());

        Booking updatedBooking = BookingTestData.updatedBooking();

        Booking result = bookingApiClient.updateBooking(bookingId, updatedBooking, authenticate())
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(result).isEqualTo(updatedBooking);

        assertBookingUnchanged(bookingId, updatedBooking);
    }

    @Test
    @DisplayName("Delete a booking and confirm it is gone")
    void shouldDeleteBooking() {
        int id = bookingApiClient.createBookingAndGetId(BookingTestData.sampleBooking());

        bookingApiClient.deleteBooking(id, authenticate())
                .then()
                .statusCode(201);

        Response getAfterDelete = bookingApiClient.getBooking(id);

        assertThat(getAfterDelete.statusCode()).isEqualTo(404);
    }

}
