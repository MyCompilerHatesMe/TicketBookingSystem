package in.gov.cgg.ticketbookingsystem.security;

import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import jakarta.annotation.Nullable;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record SecurityUser (AuthUser authUser) implements UserDetails {

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authUser.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return authUser.getPassword();
    }

    @Override
    @NullMarked
    public String getUsername () {
        return authUser.getUsername();
    }
}
