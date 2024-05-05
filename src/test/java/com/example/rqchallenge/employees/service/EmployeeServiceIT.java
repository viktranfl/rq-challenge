package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.dto.Employee;
import com.example.rqchallenge.employees.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class EmployeeServiceIT {
    public static final Logger log = LoggerFactory.getLogger(EmployeeServiceTest.class);

    @Value("${dummy.api.url}")
    private String DUMMY_API_URL;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testGetEmployees() {
        // Act
        List<Employee> employees = employeeService.getEmployees();

        // Assert
        assertEquals(24, employees.size());
    }

    @Test
    void testGetEmployeeByName() {
        // Act
        List<Employee> result = employeeService.getEmployeeByName("Tiger Nixon");

        // Assert
        assertEquals(1, result.size());
        for(Employee employee : result) {
            assertEquals("Tiger Nixon", employee.getEmployee_name());
        }
    }

    @Test
    void testGetEmployeeByNameNotFound() {
        // Act
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
           employeeService.getEmployeeByName("Lion Nixon");
        });

        String expectedMessage = "Employee not found for name: Lion Nixon";
        String actualMessage = exception.getMessage();

        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getEmployeeById() {
        // Act
        Employee result = employeeService.getEmployeeById("20");

        // Assert
        assertEquals("Dai Rios", result.getEmployee_name());
    }

    @Test
    void getHighestSalaryOfEmployees() {
        // Act
        Integer result = employeeService.getHighestSalaryOfEmployees();

        // Assert
        assertEquals(725000, result);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames() {
        // Arrange
        String[] expected = {"Paul Byrd","Yuri Berry","Charde Marshall","Cedric Kelly","Tatyana Fitzpatrick", "Brielle Williamson", "Jenette Caldwell", "Quinn Flynn", "Rhona Davidson", "Tiger Nixon"};
        // Act
        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(10, topTenHighestEarningEmployeeNames.size());
        for (String name : expected) {
            assertTrue(topTenHighestEarningEmployeeNames.contains(name));
        }
    }

    @Test
    void createEmployee() {
        // Arrange
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
    }

    @Test
    void deleteEmployee() {
        // Act
        String result = employeeService.deleteEmployee("20");

        // Assert
        assertEquals("Dai Rios", result);
    }
}