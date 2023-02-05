package com.hasitha.employeemanagment.controller;

import com.hasitha.employeemanagment.dto.AuthenticationRequest;
import com.hasitha.employeemanagment.dto.EmployeeResponseDTO;
import com.hasitha.employeemanagment.entity.Employee;
import com.hasitha.employeemanagment.service.EmployeeService;
import com.hasitha.employeemanagment.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee_jwt")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

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

    @PostMapping("/authenticate")
    public String authRequest(@RequestBody AuthenticationRequest authenticationRequest) throws UsernameNotFoundException{
            if(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword())).isAuthenticated()){
                return jwtService.generateToken(authenticationRequest.getUserName());
            }else{
                throw new UsernameNotFoundException("Invalid user credentials...");
            }
    }
}
