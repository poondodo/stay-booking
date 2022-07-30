package com.chunchun.staybooking.exception;

public class ReservationCollisionException extends RuntimeException {
    public ReservationCollisionException(String message) {
        super(message);
    }
}
