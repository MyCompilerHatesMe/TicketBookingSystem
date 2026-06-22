package in.gov.cgg.ticketbookingsystem.exception;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String message) {
        super("Auth User with username '" + message + "' exists");
    }
}
