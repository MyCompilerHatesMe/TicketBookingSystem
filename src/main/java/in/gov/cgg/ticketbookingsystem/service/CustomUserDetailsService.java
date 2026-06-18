package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.UserNotFoundException;
import in.gov.cgg.ticketbookingsystem.repository.AuthUserRepo;
import in.gov.cgg.ticketbookingsystem.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private AuthUserRepo authUserRepo;

    @Override
    @NullMarked
    public SecurityUser loadUserByUsername(String username) throws UserNotFoundException {
        return authUserRepo.findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

}
