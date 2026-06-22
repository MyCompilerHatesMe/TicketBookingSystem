package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RegisterRequest(
        @NotBlank(message = "Register request name must not be blank") String name,
        @NotBlank(message = "Register request password must not be blank") String password,
        @NotEmpty(message = "At least one role must be provided") Set<String> roles
) {
}