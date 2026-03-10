package org.example.springmvc.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Order(1)
public class NonceFilter extends OncePerRequestFilter {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        String nonce = base64Encoder.encodeToString(randomBytes);

        request.setAttribute("cspNonce", nonce);

        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'nonce-" + nonce + "'; " +
                        "style-src 'self'; " +
                        "object-src 'none'; " +
                        "base-uri 'self';");

        filterChain.doFilter(request, response);
    }
}