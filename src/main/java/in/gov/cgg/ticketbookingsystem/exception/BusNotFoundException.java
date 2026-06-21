package in.gov.cgg.ticketbookingsystem.exception;

public class BusNotFoundException extends RuntimeException {
    public BusNotFoundException(String message) {
        super(message);
    }
}
