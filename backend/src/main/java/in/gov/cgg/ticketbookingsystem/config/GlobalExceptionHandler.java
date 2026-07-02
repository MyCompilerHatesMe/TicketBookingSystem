package in.gov.cgg.ticketbookingsystem.config;

import in.gov.cgg.ticketbookingsystem.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ponytail: error payload record
    public record ErrorResponse(String error, String message) {}

    // 404 Not Found
    @ExceptionHandler({
            BusNotFoundException.class,
            RouteNotFoundException.class,
            UserNotFoundException.class,
            TripNotFoundException.class,
            SeatNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    // 400 Bad Request
    @ExceptionHandler({
            TooManySeatsException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    // 409 Conflict
    @ExceptionHandler(SeatAlreadyOccupiedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyOccupiedException(SeatAlreadyOccupiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("SEAT_OCCUPIED", ex.getMessage()));
    }

    // 409 Username/Account Exists
    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameExistsException(UsernameExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("USERNAME_EXISTS", ex.getMessage()));
    }

    // 303 See Other (Preserves BookingController fallback strategy)
    @ExceptionHandler(AccountExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountExistsException(AccountExistsException ex) {
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .body(new ErrorResponse("ACCOUNT_EXISTS", ex.getMessage()));
    }

    // 401 Unauthorized
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedException(UnauthenticatedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_CREDENTIALS", ex.getMessage()));
    }

    // 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("ACCESS_DENIED", ex.getMessage()));
    }

    // 400 Validation Failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_FAILED", message));
    }

    // 502 Bad Gateway
    @ExceptionHandler(TourismApiException.class)
    public ResponseEntity<ErrorResponse> handleTourismApiException(TourismApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("EXTERNAL_API_ERROR", ex.getMessage()));
    }

    // 500 Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // ponytail: catch-all fallback returning a standard internal error response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred."));
    }
}
