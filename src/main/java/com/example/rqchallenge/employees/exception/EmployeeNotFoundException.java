package com.example.rqchallenge.employees.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee not found for id: " + id);
    }
    public EmployeeNotFoundException(String name) {
        super("Employee not found for name: " + name);
    }
}
