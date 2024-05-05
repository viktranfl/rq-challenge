package com.example.rqchallenge.employees.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeesResponse {
    private String status;
    private List<Employee> data;
}
