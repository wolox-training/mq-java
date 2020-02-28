package wolox.training.services;

import static wolox.training.configuration.ServerSecurityConfig.isPasswordValid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import wolox.training.exceptions.EntityNotFoundException;
import wolox.training.models.User;
import org.springframework.security.core.GrantedAuthority;
import wolox.training.repositories.UserRepository;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user = userRepository.findByUsername(userName)
            .orElseThrow(()-> new EntityNotFoundException(User.class));
        if (!isPasswordValid(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        List<GrantedAuthority> grants = new ArrayList<>(
            Arrays.asList(new SimpleGrantedAuthority(user.getRole()))
        );
        return new UsernamePasswordAuthenticationToken(userName, password, grants);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}