package com.hasitha.employeemanagment.dto;

import com.hasitha.employeemanagment.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

    private String message;
    private Employee employee;
}
