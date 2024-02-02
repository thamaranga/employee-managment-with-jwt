package com.hasitha.employeemanagment.config;

import com.hasitha.employeemanagment.filter.JWTAuthFilter;
import com.hasitha.employeemanagment.service.EmployeeUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
/*Enable method level security for Roles*/
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigUpdated {


    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    /*This password encorder will encrypt/decrypt passwords for us*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*This method fetches the user details from the db*/
    @Bean
    public UserDetailsService userDetailsService() {


        return new EmployeeUserDetailsService();

    }



    /*Enable traffic for /employee_jwt/welcome , /employee_jwt/create , /employee_jwt/authenticate , /employee_jwt/generateNewAccessTokenUsingRefreshToken url endpoint without doing any user authentication.
    For all other url endpoints which starts with /employee_jwt  authenticate the user */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        return http.csrf().disable().
                authorizeRequests().
                antMatchers("/employee_jwt/welcome", "/employee_jwt/create", "/employee_jwt/authenticate" , "/employee_jwt/generateNewAccessTokenUsingRefreshToken").permitAll()
                .and()
                .authorizeRequests().antMatchers("/employee_jwt/*")
                .authenticated().and().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().authenticationProvider(authenticationProvider()).

                        addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    /*Authentication is done by this*/
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    /*Actual authentication is done by this authentication provider*/
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
}
