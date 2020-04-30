package com.tericcabrel.parking.utils;

import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tericcabrel.parking.utils.Constants.*;

/**
 * This an utility class to perform some actions associated to JWT Token
 */
@Component
public class JwtTokenUtil implements Serializable {

    @Value("${app.jwt.secret.key}")
    private String jwtSecretKey;

    /**
     * @param token JWT String
     *
     * @return the username extracted from the JWT String otherwise throw an exception
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * @param token JWT String
     *
     * @return the date of expiration of the JWT String otherwise throw an exception
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * @param token JWT String
     *
     * @return the claims extracted from the JWT String otherwise throw an exception
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);
    }

    /**
     * @param token JWT String
     *
     * @return All claims extracted from the JWT String otherwise throw an exception
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token JWT String
     *
     * @return true if the JWT string has expired otherwise false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }

    /**
     * @param authentication instance of Authentication
     *
     * @return a JWT string. Instance of Authentication is used to get authorities(user's roles) of authenticated user
     */
    public String createTokenFromAuth(Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        return generateToken(authentication.getName(), authorities);
    }

    /**
     * @param user instance of User
     *
     * @return a JWT string. Instance of User is used to get authorities(user's roles) of authenticated user
     */
    public String createTokenFromUser(User user) {
        String authorities = user.getRoles().stream().map(Role::getName).collect(Collectors.joining());

        return generateToken(user.getEmail(), authorities);
    }

    /**
     * Create a JWT String
     *
     * @param username username to be used as the subject
     * @param authorities user's authorities (roles)
     *
     * @return JWT string
     */
    private String generateToken(String username, String authorities) {
        long currentTimestampInMillis = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(new Date(currentTimestampInMillis))
                .setExpiration(new Date(currentTimestampInMillis + (TOKEN_LIFETIME_SECONDS * 1000)))
                .compact();
    }

    /**
     * @param token JWT String to validate
     * @param userDetails Instance of UserDetails
     *
     * @return true if the token is valid otherwise false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * When login is successful, this object is created in background to represent the authenticated user
     * Http is stateless so after the login response the app doesn't who is the authenticated user so, when we
     * a request with a valid token in the header, we need to recreate the object that represent the
     * authenticated use. It's the purpose of this method.
     * We can see it in action in CustomAuthenticationFilter
     *
     * @param token JWT token received from the header
     * @param existingAuth Current Authentication instance
     * @param userDetails Instance of userDetails
     *
     * @return an instance of UsernamePasswordAuthenticationToken representing the authenticated user
     */
    public UsernamePasswordAuthenticationToken getAuthentication(
        final String token, final Authentication existingAuth, final UserDetails userDetails
    ) {
        final JwtParser jwtParser = Jwts.parser().setSigningKey(jwtSecretKey);

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}
