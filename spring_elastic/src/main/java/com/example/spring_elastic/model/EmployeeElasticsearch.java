package com.example.spring_elastic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "employees") // Only 1 type: EmployeeElasticsearch
public class EmployeeElasticsearch {

    @Id
    private String id;
    private String name;
    private String department;
    private double salary;

    // Constructors
    public EmployeeElasticsearch() {}

    public EmployeeElasticsearch(String id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
