package in.gov.cgg.ticketbookingsystem.exception;

public class TooManySeatsException extends RuntimeException {
    public TooManySeatsException(String message) {
        super("Buses may only have 52 seats. " + message + " is greater than 52");
    }
}
