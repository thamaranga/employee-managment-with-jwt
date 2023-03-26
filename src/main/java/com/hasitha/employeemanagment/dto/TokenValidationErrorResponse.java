package com.hasitha.employeemanagment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenValidationErrorResponse {

    private String errorCode;
    private String errorMsg;

}
