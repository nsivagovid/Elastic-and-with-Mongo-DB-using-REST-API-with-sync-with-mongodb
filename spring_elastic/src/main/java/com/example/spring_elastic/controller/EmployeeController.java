package com.example.spring_elastic.controller;

import com.example.spring_elastic.model.EmployeeElasticsearch;
import com.example.spring_elastic.model.EmployeeMongo;
import com.example.spring_elastic.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Add Employee to MongoDB and Elasticsearch
    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody EmployeeMongo employee) {
        try {
            employeeService.saveEmployee(employee);
            return ResponseEntity.ok("Employee added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error syncing with Elasticsearch: " + e.getMessage());
        }
    }

    // Get Employee from MongoDB by ID
    @GetMapping("/mongo/{id}")
    public ResponseEntity<EmployeeMongo> getEmployeeFromMongoDBById(@PathVariable String id) {
        Optional<EmployeeMongo> employee = employeeService.getEmployeeFromMongoDBById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get Employee from Elasticsearch by ID
    @GetMapping("/elastic/{id}")
    public ResponseEntity<EmployeeElasticsearch> getEmployeeFromElasticsearchById(@PathVariable String id) {
        try {
            Optional<EmployeeElasticsearch> employee = employeeService.getEmployeeFromElasticsearchById(id);
            return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all employees from MongoDB
    @GetMapping("/mongo/all")
    public ResponseEntity<List<EmployeeMongo>> getAllEmployeesFromMongo() {
        List<EmployeeMongo> employees = employeeService.getAllEmployeesFromMongo();
        return ResponseEntity.ok(employees); // Return list of employees
    }

    // Delete Employee by ID from MongoDB and Elasticsearch
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        try {
            employeeService.deleteEmployeeById(id);
            return ResponseEntity.ok("Employee deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting from Elasticsearch: " + e.getMessage());
        }
    }
}
