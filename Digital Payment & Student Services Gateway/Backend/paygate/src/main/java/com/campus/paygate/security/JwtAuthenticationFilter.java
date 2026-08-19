package com.campus.paygate.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    // 1. Inject your token provider
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String header = request.getHeader("Authorization");
            
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                
                // 2. ACTUALLY VALIDATE THE TOKEN!
                // (Note: If your method is named something else like isValidToken, change it here)
                if (tokenProvider.validateToken(token)) {
                    
                    // 3. Get the matric number from the token
                    // (Change getUsernameFromToken to whatever method you wrote in JwtTokenProvider)
                    String username = tokenProvider.getIdentifierFromJWT(token); 
                    
                    // 4. Create the official Spring Security VIP pass
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
                        );
                    
                    // 5. STAMP IT! This is what prevents the 403 Forbidden.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            
        } catch (Exception e) {
            System.err.println("JWT FILTER ERROR: " + e.getMessage());
        }

        // Hand the request off to the Controller
        filterChain.doFilter(request, response); 
    }
}