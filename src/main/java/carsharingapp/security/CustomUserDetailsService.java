package carsharingapp.security;

import carsharingapp.exception.EntityNotFoundException;
import carsharingapp.model.User;
import carsharingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User " + email + " not found"));
    }

    public User getUserFromAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID "
                        + user.getId() + " not found"));
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
