package com.cognizant.uams.security;

import com.cognizant.uams.entity.User;
import com.cognizant.uams.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private record Session(Integer userId, Instant expiresAt) {}
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String issue(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(user.getUserId(), Instant.now().plus(12, ChronoUnit.HOURS)));
        return token;
    }

    public Optional<UsernamePasswordAuthenticationToken> authenticate(String token) {
        Session session = sessions.get(token);
        if (session == null || session.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return userRepository.findById(session.userId()).map(user ->
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        token,
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                )
        );
    }

    public void revoke(String token) {
        sessions.remove(token);
    }
}
