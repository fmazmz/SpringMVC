package org.example.springmvc.exceptions;

public class ErrorMessages {

    private ErrorMessages() {}

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";

    public static final String USER_NOT_FOUND_ID = "User not found with id %s";
    public static final String USER_NOT_FOUND_EMAIL = "User not found with email %s";
    public static final String DUPLICATE_USER_EMAIL = "Unable to create an account with this email";

    public static final String DRIVER_NOT_FOUND_ID = "Driver not found with id %s";
    public static final String DRIVER_SSN_EXISTS = "Driver with this SSN already exists";
    public static final String USER_ALREADY_DRIVER = "User is already a driver";

    public static final String CAR_NOT_FOUND_ID = "Car not found with id %s";
    public static final String CAR_DUPLICATE_PLATE = "A car with license plate '%s' already exists";
    public static final String CAR_DUPLICATE_VIN = "A car with VIN '%s' already exists";

    public static final String BOOKING_NOT_FOUND_ID = "Booking not found with id %s";
    public static final String INVALID_BOOKING_TIME = "Start time must be before end time";
    public static final String BOOKING_DUPLICATE = "Car already booked for that time";
    public static final String UNAUTHORIZED_BOOKING_ACTION = "You can only cancel your own bookings";
}