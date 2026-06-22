package in.gov.cgg.ticketbookingsystem.exception;

public class SeatAlreadyOccupiedException extends RuntimeException {
    public SeatAlreadyOccupiedException(String message) {
        super(message);
    }
}
