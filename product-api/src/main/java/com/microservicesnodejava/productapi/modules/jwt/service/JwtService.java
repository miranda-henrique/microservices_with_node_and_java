package com.microservicesnodejava.productapi.modules.jwt.service;

import com.microservicesnodejava.productapi.config.exception.AuthenticationException;
import com.microservicesnodejava.productapi.modules.jwt.dto.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class JwtService {

    private static final String BEARER = "Bearer ";

    @Value("${API_SECRET}")
    private String apiSecret;

    public void validateAuthorization(String token) {
        var accessToken = extractToken(token);
        System.out.println(token);
        try {
            var claims = Jwts
                    .parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(apiSecret.getBytes()))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            var user = JwtResponse.getUser(claims);

            if (isEmpty(user) || isEmpty(user.getId())) {
                throw new AuthenticationException("Invalid user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationException("Error while trying to process access token");
        }
    }

    private String extractToken(String token) {
        if (isEmpty(token)) {
            throw new AuthenticationException("Access token was not informed");
        }

        if (token.contains(BEARER)) {
            String aux = token.split(" ")[1];
            return aux;
        }

        return token;
    }
}
