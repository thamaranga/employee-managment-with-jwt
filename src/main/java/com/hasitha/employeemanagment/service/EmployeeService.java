package com.hasitha.employeemanagment.service;

import com.hasitha.employeemanagment.dto.EmployeeResponseDTO;
import com.hasitha.employeemanagment.entity.Employee;
import com.hasitha.employeemanagment.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Employee createEmployee(Employee employee){
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setRole("ROLE_EMPLOYEE");
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public EmployeeResponseDTO getEmployeeById(int id){
        Optional<Employee> optionalEmployee=employeeRepository.findById(id);
        Employee employee=null;
        EmployeeResponseDTO employeeResponseDTO= new EmployeeResponseDTO();
        if(optionalEmployee.isPresent()){
            employee=optionalEmployee.get();
            employeeResponseDTO.setMessage("success");
            employeeResponseDTO.setEmployee(employee);
        }else{
            employeeResponseDTO.setMessage("No employee data found for given id : "+ id);
        }
        return employeeResponseDTO;
    }

    public EmployeeResponseDTO changeRoleOfEmployee(Employee employee){
        EmployeeResponseDTO result= new EmployeeResponseDTO();
        EmployeeResponseDTO employeeResponseDTO=this.getEmployeeById(employee.getId());
        if(employeeResponseDTO.getEmployee()!=null){
            Employee existingEmployee=employeeResponseDTO.getEmployee();
            existingEmployee.setRole(existingEmployee.getRole()+"," +
                    ""+employee.getRole());
            Employee updatedEmployee=employeeRepository.save(existingEmployee);
            result.setEmployee(updatedEmployee);
            result.setMessage("success");
        }else{
            result.setMessage("No existing employee found...");
        }
        return result;

    }


}
