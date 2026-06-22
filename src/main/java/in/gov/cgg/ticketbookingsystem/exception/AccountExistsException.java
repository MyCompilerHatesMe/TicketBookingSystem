package in.gov.cgg.ticketbookingsystem.exception;

public class AccountExistsException extends RuntimeException {
    public AccountExistsException(String message) {
        super(message);
    }
}
