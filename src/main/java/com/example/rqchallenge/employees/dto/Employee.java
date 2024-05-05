package com.example.rqchallenge.employees.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Details about the Employee")
public class Employee implements Serializable {
    private Long id;
    @Schema(description = "Name of the employee", example = "John Doe", required = true)
    @JsonAlias("name")
    private String employee_name;
    @Schema(description = "Salary of the employee", example = "50000", required = true)
    @JsonAlias("salary")
    private Integer employee_salary;
    @Schema(description = "Age of the employee", example = "30", required = true)
    @JsonAlias("age")
    private Integer employee_age;
    private String profile_image;

    // Getters and setters for each field
}
