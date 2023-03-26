package com.hasitha.employeemanagment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasitha.employeemanagment.dto.AuthenticationRequest;
import com.hasitha.employeemanagment.dto.EmployeeResponseDTO;
import com.hasitha.employeemanagment.dto.TokenResponseDTO;
import com.hasitha.employeemanagment.dto.TokenValidationErrorResponse;
import com.hasitha.employeemanagment.entity.Employee;
import com.hasitha.employeemanagment.service.EmployeeService;
import com.hasitha.employeemanagment.service.EmployeeUserDetailsService;
import com.hasitha.employeemanagment.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee_jwt")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    EmployeeUserDetailsService employeeUserDetailsService;

    @PostMapping("/create")
    //This method can be accessed for any user.
    public Employee addEmployee(@RequestBody Employee employee){
        return employeeService.createEmployee(employee);
    }

    @GetMapping("/getAll")
    /*
    * This method can be accessed for both users with the ROLE of HR or MANAGER
    * */
    @PreAuthorize("hasAuthority('ROLE_HR') or hasAuthority('ROLE_MANAGER')")
    public List<Employee> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public EmployeeResponseDTO getEmployeeById(@PathVariable int id){
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to Hasitha Estate PVT LTD.";
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public EmployeeResponseDTO updateEmployeeRole(@RequestBody Employee employee){
        return employeeService.changeRoleOfEmployee(employee);
    }

    /*This method validates the user details. If its valid then application will
    * generate  jason web tokens (Access token and refresh token)*/
    @PostMapping("/authenticate")
    public TokenResponseDTO authRequest(@RequestBody AuthenticationRequest authenticationRequest) throws UsernameNotFoundException{
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword()));
                if (authentication.isAuthenticated()) {
                    return jwtService.generateToken(authentication);
                } else {
                    System.out.println("Authentication failure");
                    throw new UsernameNotFoundException("********* Invalid user credentials...****");
                }
            }catch(Exception ex){
                System.out.println("Exception occured *******"+ex.getMessage());
                throw new UsernameNotFoundException("********* Invalid user credentials...****");
            }

    }

    /*This method is generated a refresh token*/
    @GetMapping("/generateNewAccessTokenUsingRefreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{


        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                //Extracts jwt token
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);

                if (username != null) {
                    UserDetails userDetails = employeeUserDetailsService.loadUserByUsername(username);
                    if (jwtService.validateToken(token, userDetails)) {

                        TokenResponseDTO tokenResponseDTO= new TokenResponseDTO();

                        Map<String , Object> claims= new HashMap<>();
                        claims.put("roles",userDetails.getAuthorities());
                        tokenResponseDTO.setAccessToken(jwtService.createAccessToken(claims, userDetails.getUsername()));
                        tokenResponseDTO.setRefreshToken(token);
                        response.setStatus(HttpStatus.OK.value());
                        response.setContentType("application/json");

                        new ObjectMapper().writeValue(response.getOutputStream(),tokenResponseDTO);




                    }
                }else{
                    System.out.println("Invalid refresh token....");
                }

            }else{
                System.out.println("Refresh token is missing....");
            }
        }
        catch(ExpiredJwtException e){
            /*If JWT token validation is failed , then 403 error code will return to frontend
             */
            System.out.println("ex "+e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            TokenValidationErrorResponse tokenValidationErrorResponse= new TokenValidationErrorResponse();
            tokenValidationErrorResponse.setErrorCode("100");
            tokenValidationErrorResponse.setErrorMsg("Refresh Token is Expired. You need to generate new  tokens by calling /authenticate endpoint.");
            new ObjectMapper().writeValue(response.getOutputStream(),tokenValidationErrorResponse);

        }

        catch(Exception ex){
            /*If JWT token validation is failed , then 403 error code will return to frontend
             * with an error message which is returned by java*/
            System.out.println("ex "+ex.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            TokenValidationErrorResponse tokenValidationErrorResponse= new TokenValidationErrorResponse();
            tokenValidationErrorResponse.setErrorCode("101");
            tokenValidationErrorResponse.setErrorMsg(ex.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(),tokenValidationErrorResponse);


        }


    }
}
