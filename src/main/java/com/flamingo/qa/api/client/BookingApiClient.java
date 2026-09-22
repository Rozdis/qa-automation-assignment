package com.flamingo.qa.api.client;

import com.flamingo.qa.api.model.auth.AuthRequest;
import com.flamingo.qa.api.model.auth.AuthResponse;
import com.flamingo.qa.api.model.booking.Booking;
import com.flamingo.qa.api.model.booking.CreateBookingResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class BookingApiClient {

    private final String baseUrl;

    public String authenticate(AuthRequest request) {
        return authenticateRaw(request)
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class)
                .getToken();
    }

    public Response authenticateRaw(AuthRequest request) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth");
    }

    public Response createBooking(Booking booking) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(booking)
                .when()
                .post("/booking");
    }

    public int createBookingAndGetId(Booking booking) {
        return createBooking(booking)
                .then()
                .statusCode(200)
                .extract()
                .as(CreateBookingResponse.class)
                .getBookingid();
    }

    public Response getBooking(int bookingId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}");
    }

    public Response updateBooking(int bookingId, Booking booking, String token) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(booking)
                .when()
                .put("/booking/{id}");
    }

    public Response deleteBooking(int bookingId, String token) {
        return given()
                .baseUri(baseUrl)
                .cookie("token", token)
                .pathParam("id", bookingId)
                .when()
                .delete("/booking/{id}");
    }
}
