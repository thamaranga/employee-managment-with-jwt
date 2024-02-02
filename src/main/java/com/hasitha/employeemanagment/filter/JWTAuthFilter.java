package com.hasitha.employeemanagment.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasitha.employeemanagment.dto.TokenValidationErrorResponse;
import com.hasitha.employeemanagment.service.EmployeeUserDetailsService;
import com.hasitha.employeemanagment.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*This filter will execute for all the requests of the application
* Basically this authenticates user for further req with access token*/
@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmployeeUserDetailsService employeeUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException   {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        try {
            //If token is not found in the header, then request will continue to usernamepassword filter. So then, if our resource has declared as no need to authenticate only we can access that resource.
            if (authHeader != null && authHeader.startsWith("Bearer") && !request.getServletPath().equals("/employee_jwt/generateNewAccessTokenUsingRefreshToken")) {
                //Extracts jwt token
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
                //SecurityContextHolder.getContext().getAuthentication() == null means current username is not authenticated
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = employeeUserDetailsService.loadUserByUsername(username);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    }
                }

            }
        }catch(ExpiredJwtException e){
            /*If JWT token validation is failed , then 403 error code will return to frontend with our custom error code 100
             */
            System.out.println("ex "+e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            TokenValidationErrorResponse tokenValidationErrorResponse= new TokenValidationErrorResponse();
            tokenValidationErrorResponse.setErrorCode("100");
            tokenValidationErrorResponse.setErrorMsg("Access Token is Expired. You need to generate new access token using refresh token.");
            new ObjectMapper().writeValue(response.getOutputStream(),tokenValidationErrorResponse);

        }

        catch(Exception ex){
            /*If JWT token validation is failed , then 403 error code will return to frontend with our custom error code 101 and
            * with an error message which is returned by java*/
            System.out.println("ex "+ex.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            TokenValidationErrorResponse tokenValidationErrorResponse= new TokenValidationErrorResponse();
            tokenValidationErrorResponse.setErrorCode("101");
            tokenValidationErrorResponse.setErrorMsg(ex.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(),tokenValidationErrorResponse);

        }
        filterChain.doFilter(request, response);
    }
}
