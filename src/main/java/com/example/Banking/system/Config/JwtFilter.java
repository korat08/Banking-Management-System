package com.example.Banking.system.Config;

import com.example.Banking.system.Model.CustomUserDetails;
import com.example.Banking.system.Service.CustomUserDetailsService;
import com.example.Banking.system.Service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private void writeErrorResponse(HttpServletResponse response, int status, String message){
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String,Object> error = new HashMap<>();
        error.put("Error", true);
        error.put("status", status);
        error.put("Message",message);

        try{
            new ObjectMapper().writeValue(response.getWriter(),error);
        }catch (IOException e){
            logger.warn("Failed to write JSON error response", e);
            try{
                response.getWriter().write("{\"error\":true,\"status\":" + status + ",\"message\":\"" + message + "\"}");
            }catch (IOException ex){
                logger.error("Final fallback also failed while writing error response", ex);
            }
        }
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            token =authHeader.substring(7);

            try{
                userName = jwtService.extractUserName(token);
            } catch (ExpiredJwtException e) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            } catch (SignatureException e) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                return;
            } catch (MalformedJwtException e) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT token");
                return;
            } catch (IllegalArgumentException e) {
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "JWT token is empty or null");
                return;
            } catch (Exception e) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        if(userName !=  null && SecurityContextHolder.getContext().getAuthentication() == null){

            CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userName);

            if(jwtService.validateToken(token,customUserDetails)){

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }else{
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
                return;
            }

        }

        filterChain.doFilter(request,response);

    }
}
