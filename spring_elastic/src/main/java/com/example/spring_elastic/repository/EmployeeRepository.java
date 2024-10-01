package com.example.spring_elastic.repository;

import com.example.spring_elastic.model.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {
}
