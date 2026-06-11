package nixdocs.backend.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nixdocs.backend.configuration.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    // Check if header exists and starts with Bearer
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    // Extract token (remove "Bearer " prefix)
    final String token = authHeader.substring(7);

    try {
      // Validate token and extract username
      if (jwtService.validateToken(token)) {
        String username = jwtService.extractSubject(token);

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Create authentication object
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null, // credentials (already authenticated)
            userDetails.getAuthorities());

        // Set authentication in Spring Security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (ParseException e) {
      // Log the error but don't throw - just don't authenticate
      logger.error("JWT validation failed: " + e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    chain.doFilter(request, response);
  }
}
