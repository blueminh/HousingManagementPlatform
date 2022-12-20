package sem.voting.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {
    @Value("${jwt.secret}")  // automatically loads jwt.secret from resources/application.properties
    private static transient String jwtSecret;

    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Constructs a token from a userId.
     *
     * @param userId userId to create the token for
     * @return a token for communication between microservices
     */
    public static String getTokenFromId(String userId) {
        Date nowDate = new Date(Instant.now().toEpochMilli());
        return Jwts.builder().setClaims(new HashMap<>()).setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(Date.from(nowDate.toInstant().plusSeconds(60 * 60)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }
}
