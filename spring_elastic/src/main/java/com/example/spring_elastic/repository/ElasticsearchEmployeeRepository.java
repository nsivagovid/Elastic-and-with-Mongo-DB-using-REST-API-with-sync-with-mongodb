package com.example.spring_elastic.repository;

import com.example.spring_elastic.model.EmployeeElasticsearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchEmployeeRepository extends ElasticsearchRepository<EmployeeElasticsearch, String> {
}
