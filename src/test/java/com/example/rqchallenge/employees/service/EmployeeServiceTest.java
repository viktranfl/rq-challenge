package com.example.rqchallenge.employees.service;
import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.dto.EmployeeResponse;
import com.example.rqchallenge.employees.dto.EmployeesResponse;
import com.example.rqchallenge.employees.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class EmployeeServiceTest {
    public static final Logger log = LoggerFactory.getLogger(EmployeeServiceTest.class);

    @Value("${dummy.api.url}")
    private String DUMMY_API_URL;

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private RestTemplate restTemplate;

    private static EmployeesResponse mockEmployeesResponse;

    @BeforeAll
    public static void setUp() {
        log.info("Setting up tests");
        List<Employee> employees = new ArrayList<>();
        String[] names = {"Jim Halpert", "Pam Beesley", "John Doe", "Jane Smith", "Steve Johnson", "Samantha Williams", "Robert Brown", "Rebecca Davis", "Michael Miller", "Michelle Wilson", "David Moore", "Danielle Taylor", "Brian Anderson", "Brenda Thomas", "Adam Jackson", "Amanda White", "Chris Harris", "Jim Halpert"};
        int[] salaries = {150000, 200000, 50000, 75000, 120000, 60000, 80000, 95000, 70000, 650000, 55000, 125000, 90000, 90000, 100000, 110000, 105000, 50000};
        for (int i = 0; i < names.length; i++) {
            Employee employee = Employee.builder()
                    .employee_name(names[i])
                    .employee_age(25 + i)
                    .employee_salary(salaries[i])
                    .id((long) i + 1)
                    .build();
            employees.add(employee);
        }
        mockEmployeesResponse = EmployeesResponse.builder().status("success").data(employees).build();
    }

    @Test
    public void testGetEmployees() {
        // Arrange

        when(restTemplate.getForObject(DUMMY_API_URL + "/employees", EmployeesResponse.class)).thenReturn(mockEmployeesResponse);
        // Act
        List<Employee> employees = employeeService.getEmployees();

        // Assert
        assertEquals(18, employees.size());
    }

    @Test
    void testGetEmployeeByName() {
        // Test with Duplicate Jims

        // Arrange
        when(restTemplate.getForObject(DUMMY_API_URL + "/employees", EmployeesResponse.class)).thenReturn(mockEmployeesResponse);

        // Act
        List<Employee> result = employeeService.getEmployeeByName("Jim Halpert");

        // Assert
        assertEquals(2, result.size());
        for(Employee employee : result) {
            assertEquals("Jim Halpert", employee.getEmployee_name());
        }

    }

    @Test
    void testGetEmployeeByNameNotFound() {
        // Dwight not in employees

        // Arrange
        when(restTemplate.getForObject(DUMMY_API_URL + "/employees", EmployeesResponse.class)).thenReturn(mockEmployeesResponse);

        // Act
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeByName("Dwight Schrute");
        });

        String expectedMessage = "Employee not found for name: Dwight Schrute";
        String actualMessage = exception.getMessage();

        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGetEmployeeById() {
        // Arrange
        Employee employee2 = Employee.builder().employee_name("Pam Beesley")
                .employee_age(25).employee_salary(200000).id(2L).build();
        EmployeeResponse mockResponse = EmployeeResponse.builder().status("success").data(
                employee2).build();

        when(restTemplate.getForObject(DUMMY_API_URL + "/employee/" + 2, EmployeeResponse.class)).thenReturn(mockResponse);

        // Act
        Employee employee = employeeService.getEmployeeById("2");

        // Assert
        assertEquals("Pam Beesley", employee.getEmployee_name());

    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        // Arrange
        when(restTemplate.getForObject(DUMMY_API_URL + "/employees", EmployeesResponse.class)).thenReturn(mockEmployeesResponse);

        // Act
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(650000, highestSalary);
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        // Test edge case where others have the same salary as the 10th ranked employee
        String[] expected = {"Jim Halpert", "Pam Beesley", "Steve Johnson", "Rebecca Davis", "Michelle Wilson","Danielle Taylor", "Brian Anderson", "Brenda Thomas", "Adam Jackson", "Amanda White", "Chris Harris"};

        // Arrange
        when(restTemplate.getForObject(DUMMY_API_URL + "/employees", EmployeesResponse.class)).thenReturn(mockEmployeesResponse);

        // Act
        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(11, topTenHighestEarningEmployeeNames.size());
        for (String name : expected) {
            assertTrue(topTenHighestEarningEmployeeNames.contains(name));
        }
    }

    @Test
    void createEmployee() {
        // Arrange
        Employee newEmployee = Employee.builder()
                .employee_name("SpongeBob SquarePants")
                .employee_age(30)
                .employee_salary(100000)
                .build();

        EmployeeResponse mockResponse = EmployeeResponse.builder().status("success").data(newEmployee).build();

        when(restTemplate.postForObject(
                eq(DUMMY_API_URL + "/create"),
                any(),
                eq(EmployeeResponse.class)
        )).thenReturn(mockResponse);

        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("name", "SpongeBob SquarePants");
        employeeMap.put("age", 30);
        employeeMap.put("salary", 100000);

        // Act
        Employee createdEmployee = employeeService.createEmployee(employeeMap);

        // Assert
        assertEquals("SpongeBob SquarePants", createdEmployee.getEmployee_name());
        assertEquals(30, createdEmployee.getEmployee_age());
        assertEquals(100000, createdEmployee.getEmployee_salary());
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(
                eq(DUMMY_API_URL + "/create"),
                any(Map.class),
                eq(EmployeeResponse.class)
        );
    }

    @Test
    void deleteEmployee() {
        // Arrange
        Employee employee2 = Employee.builder().employee_name("Pam Beesley")
                .employee_age(25).employee_salary(200000).id(2L).build();
        EmployeeResponse mockResponse = EmployeeResponse.builder().status("success").data(
                employee2).build();

        when(restTemplate.getForObject(DUMMY_API_URL + "/employee/" + 2, EmployeeResponse.class)).thenReturn(mockResponse);

        // Act
        String responseMessage = employeeService.deleteEmployee("2");

        // Assert
        Mockito.verify(restTemplate, Mockito.times(1)).delete(DUMMY_API_URL + "/delete/" + 2);
        assertEquals("Pam Beesley", responseMessage);
    }
}