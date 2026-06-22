package in.gov.cgg.ticketbookingsystem.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User with auth username " + message + " not found");
    }
}
