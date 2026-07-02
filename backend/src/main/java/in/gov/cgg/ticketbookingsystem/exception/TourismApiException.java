package in.gov.cgg.ticketbookingsystem.exception;

public class TourismApiException extends RuntimeException {
    public TourismApiException(String message) {
        super(message);
    }

    public TourismApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
