package com.flamingo.qa.api.tests.booking.api;

import com.flamingo.qa.api.model.booking.Booking;
import com.flamingo.qa.api.model.booking.BookingDates;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class BookingTestData {

    static Booking sampleBooking() {
        return Booking.builder()
                .firstname("Test")
                .lastname("Book")
                .totalprice(100)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2026-01-01")
                        .checkout("2026-12-12")
                        .build())
                .additionalneeds("Testing")
                .build();
    }

    static Booking updatedBooking() {
        return Booking.builder()
                .firstname("Updated")
                .lastname("TestBook")
                .totalprice(200)
                .depositpaid(false)
                .bookingdates(BookingDates.builder()
                        .checkin("2026-02-01")
                        .checkout("2026-02-10")
                        .build())
                .additionalneeds("Updated field")
                .build();
    }

    static Stream<Booking> bookingProvider() {
        return Stream.of(
                sampleBooking(),
                Booking.builder()
                        .firstname("Jane")
                        .lastname("Smith")
                        .totalprice(250)
                        .depositpaid(false)
                        .bookingdates(BookingDates.builder()
                                .checkin("2026-03-15")
                                .checkout("2026-03-20")
                                .build())
                        .additionalneeds("Breakfast")
                        .build(),
                Booking.builder()
                        .firstname("Ana")
                        .lastname("Silva")
                        .totalprice(0)
                        .depositpaid(true)
                        .bookingdates(BookingDates.builder()
                                .checkin("2027-01-01")
                                .checkout("2027-01-02")
                                .build())
                        .additionalneeds("")
                        .build()
        );
    }
}
