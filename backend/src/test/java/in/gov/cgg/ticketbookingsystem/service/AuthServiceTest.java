package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.UsernameExistsException;
import in.gov.cgg.ticketbookingsystem.model.Role;
import in.gov.cgg.ticketbookingsystem.model.dto.request.LoginRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RegisterRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RegisterResponse;
import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import in.gov.cgg.ticketbookingsystem.repository.AuthUserRepo;
import in.gov.cgg.ticketbookingsystem.security.SecurityUser;
import in.gov.cgg.ticketbookingsystem.utility.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthUserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private DtoMapper mapper;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest(
                "newuser", "password123", "New User", "newuser@example.com", "9876543210", Set.of("ROLE_USER")
        );

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        AuthUser savedUser = new AuthUser();
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(Role.ROLE_USER));

        when(userRepo.save(any(AuthUser.class))).thenReturn(savedUser);
        when(mapper.toResponse(any(AuthUser.class))).thenReturn(new RegisterResponse("newuser", Set.of("ROLE_USER")));

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("newuser", response.username());
        assertTrue(response.roles().contains("ROLE_USER"));
        verify(userRepo, times(1)).save(any(AuthUser.class));
    }

    @Test
    void register_invalidRoleAdmin_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "adminuser", "password123", "Admin User", "admin@example.com", "9876543210", Set.of("ROLE_ADMIN")
        );

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepo, never()).save(any(AuthUser.class));
    }

    @Test
    void register_usernameExists_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "existinguser", "password123", "Existing User", "existing@example.com", "9876543210", Set.of("ROLE_USER")
        );

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepo.save(any(AuthUser.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThrows(UsernameExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        Authentication auth = mock(Authentication.class);
        
        AuthUser authUser = new AuthUser();
        authUser.setUsername("testuser");
        authUser.setRoles(Set.of(Role.ROLE_USER));
        SecurityUser securityUser = new SecurityUser(authUser);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(securityUser);
        when(jwtService.generateToken(eq("testuser"), eq(Set.of(Role.ROLE_USER)))).thenReturn("mock-jwt-token");

        String token = authService.login(request);

        assertNotNull(token);
        assertEquals("mock-jwt-token", token);
        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_badCredentials_throwsException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
