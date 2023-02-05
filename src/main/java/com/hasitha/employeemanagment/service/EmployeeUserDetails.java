package com.hasitha.employeemanagment.service;

import com.hasitha.employeemanagment.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeUserDetails implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorityList;

    public EmployeeUserDetails(Employee employee){
        this.username=employee.getUserName();
        this.password=employee.getPassword();
        String roles=employee.getRole();
        String[] rolesArray=roles.split(",");
        for (String r:rolesArray) {
            System.out.println("r "+r);
        }
        authorityList=Arrays.stream(rolesArray).map(x-> new SimpleGrantedAuthority(x)).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
