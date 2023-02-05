package com.hasitha.employeemanagment.service;

import com.hasitha.employeemanagment.entity.Employee;
import com.hasitha.employeemanagment.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee=employeeRepository.findByUserName(username);
        if(optionalEmployee.isPresent()){
            return new EmployeeUserDetails(optionalEmployee.get());
        }else{
            throw new UsernameNotFoundException("Requested User Details Not Found");
        }

    }
}
