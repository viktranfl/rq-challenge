package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.dto.EmployeeResponse;
import com.example.rqchallenge.employees.dto.EmployeesResponse;
import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.exception.EmployeeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeService {
    @Value("${dummy.api.url}")
    private String DUMMY_API_URL;

    private final RestTemplate restTemplate;

    // Use caching if we had exclusive access to the dummy API
    private static final String EMPLOYEES_CACHE = "employees";
    private static final String HIGHEST_SALARY_CACHE = "highestSalary";
    private static final String TOP_TEN_HIGHEST_EARNING_EMPLOYEE_NAMES_CACHE = "topTenHighestEarningEmployeeNames";

    @Autowired
    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Cacheable(EMPLOYEES_CACHE)
    public List<Employee> getEmployees() {
        log.info("Fetching all employees from the API");
        EmployeesResponse response = restTemplate.getForObject(DUMMY_API_URL + "/employees", EmployeesResponse.class);
        return response != null ? response.getData() : Collections.emptyList();
    }

    public List<Employee> getEmployeeByName(String name) {
        log.info("Fetching employee with name: {}", name);
        List<Employee> employees = getEmployees();
        List<Employee> matchingEmployees = employees.stream()
                .filter(employee -> employee.getEmployee_name().equals(name)).collect(Collectors.toList());
        if(matchingEmployees.isEmpty()){
            throw new EmployeeNotFoundException(name);
        }
        return matchingEmployees;
    }
    public Employee getEmployeeById(String id) {
        log.info("Fetching employee with ID: {}", id);
        EmployeeResponse response = restTemplate.getForObject(DUMMY_API_URL + "/employee/" + id, EmployeeResponse.class);
        if (response == null || response.getData() == null) {
            throw new EmployeeNotFoundException(Long.parseLong(id));
        }
        return response.getData();
    }

    @Cacheable(HIGHEST_SALARY_CACHE)
    public Integer getHighestSalaryOfEmployees() {
        List<Employee> employees = getEmployees();
        int maxSalary = 0;
        for (Employee employee : employees) {
            maxSalary = Math.max(maxSalary, employee.getEmployee_salary());
        }
        return maxSalary;
    }

    @Cacheable(TOP_TEN_HIGHEST_EARNING_EMPLOYEE_NAMES_CACHE)
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employees = getEmployees();
        employees.sort((e1, e2) -> e2.getEmployee_salary() - e1.getEmployee_salary());
        List<Employee> topEarners = new ArrayList<>();
        int tenthRankedSalary = 0;
        for (int i = 0; i < employees.size(); i++) {
            if(i==9){
                tenthRankedSalary = employees.get(i).getEmployee_salary();
            }
            if (i > 9 && employees.get(i).getEmployee_salary() < tenthRankedSalary) {
                // include all employees with the same salary as the 10th ranked employee
                break;
            }
            topEarners.add(employees.get(i));
        }
        if (log.isDebugEnabled()) {
            topEarners.forEach(earner -> log.debug("Top earner: {}, Salary: {}", earner.getEmployee_name(), earner.getEmployee_salary()));
        }
        return topEarners.stream()
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {EMPLOYEES_CACHE, HIGHEST_SALARY_CACHE, TOP_TEN_HIGHEST_EARNING_EMPLOYEE_NAMES_CACHE}, allEntries = true)
    public Employee createEmployee(Map<String, Object> employeeInput) {

        log.info("Creating new employee with input: {}", employeeInput);
        EmployeeResponse response = restTemplate.postForObject(DUMMY_API_URL + "/create", employeeInput, EmployeeResponse.class);
        return response != null ? response.getData() : null;
    }

    @CacheEvict(value = {EMPLOYEES_CACHE, HIGHEST_SALARY_CACHE, TOP_TEN_HIGHEST_EARNING_EMPLOYEE_NAMES_CACHE}, allEntries = true)
    public String deleteEmployee(String id) {
        log.info("Deleting employee with ID: {}", id);
        Employee employeeToDelete = getEmployeeById(id);
        restTemplate.delete(DUMMY_API_URL + "/delete/" + id);
        return employeeToDelete.getEmployee_name();
    }

}