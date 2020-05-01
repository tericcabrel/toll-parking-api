package com.tericcabrel.parking.utils;

import com.tericcabrel.parking.models.dbs.Role;
import com.tericcabrel.parking.models.dbs.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.tericcabrel.parking.utils.Constants.*;
import static com.tericcabrel.parking.utils.Constants.TOKEN_LIFETIME_SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.OrderAnnotation.class)
@ContextConfiguration(classes = JwtTokenUtil.class)
@ExtendWith({SpringExtension.class})
class JwtTokenUtilTest {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String username;

    private String token;

    private User user;

    private UserDetails userDetails;

    private Authentication authentication;

    @BeforeAll
    void beforeAll() {
        username = "test@test.com";

        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name(ROLE_ADMIN).build());
        roles.add(Role.builder().name(ROLE_USER).build());

        user = User.builder()
                    .email(username)
                    .name("Test User")
                    .password("12345678")
                    .roles(roles)
                    .build();

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Test
    @Order(1)
    void generateTokenFromUser() {
        token = jwtTokenUtil.createTokenFromUser(user);

        assertThat(token).isNotBlank();
        assertThat(token).isInstanceOf(String.class);
    }

    @Test
    @Order(2)
    void validateToken() {
        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @Order(3)
    void getUsernameFromToken() {
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token);

        assertThat(usernameFromToken).isEqualTo(username);
    }

    @Test
    @Order(4)
    void getExpirationDateFromToken() {
        Date dateFromToken = jwtTokenUtil.getExpirationDateFromToken(token);

        assertThat(dateFromToken.getTime()).isGreaterThan(new Date().getTime());

        long currentTimestampInMillis = System.currentTimeMillis();

        assertThat(dateFromToken.getTime())
            .isLessThan(new Date(currentTimestampInMillis + (TOKEN_LIFETIME_SECONDS * 1000)).getTime());

    }

    @Test
    @Order(5)
    void getAuthenticationFromToken() {
        authentication = jwtTokenUtil.getAuthentication(token, null, userDetails);

        assertThat(authentication.getName()).isEqualTo(username);
    }

    @Test
    @Order(6)
    void generateTokenFromAuthentication() {
        String tokenAuthentication = jwtTokenUtil.createTokenFromAuth(authentication);

        assertThat(tokenAuthentication).isNotBlank();
        assertThat(tokenAuthentication).isInstanceOf(String.class);

        String usernameToken = jwtTokenUtil.getUsernameFromToken(token);
        String usernameAuthentication = jwtTokenUtil.getUsernameFromToken(tokenAuthentication);

        assertThat(usernameToken).isEqualTo(usernameAuthentication);
    }
}