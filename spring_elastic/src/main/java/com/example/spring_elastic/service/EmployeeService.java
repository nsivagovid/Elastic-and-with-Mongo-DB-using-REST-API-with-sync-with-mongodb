package com.example.spring_elastic.service;

import com.example.spring_elastic.model.EmployeeMongo;
import com.example.spring_elastic.model.EmployeeElasticsearch;
import com.example.spring_elastic.repository.EmployeeMongoRepository;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMongoRepository mongoRepository;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    // Save Employee to MongoDB and Elasticsearch
    public String saveEmployee(EmployeeMongo employeeMongo) throws IOException {
        // Save to MongoDB
        EmployeeMongo savedEmployee = mongoRepository.save(employeeMongo);

        // Create an IndexRequest to save to Elasticsearch
        IndexRequest indexRequest = new IndexRequest("employees")
                .id(savedEmployee.getId()) // Setting ID manually
                .source("name", savedEmployee.getName(),
                        "department", savedEmployee.getDepartment(),
                        "salary", savedEmployee.getSalary());

        // Save to Elasticsearch
        IndexResponse indexResponse = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);

        return savedEmployee.getId(); // Return ID after successful save
    }



    // Get Employee by ID from MongoDB
    public Optional<EmployeeMongo> getEmployeeFromMongoDBById(String id) {
        return mongoRepository.findById(id);
    }

    // Get Employee by ID from Elasticsearch
    public Optional<EmployeeElasticsearch> getEmployeeFromElasticsearchById(String id) throws IOException {
        // Create GetRequest for Elasticsearch
        GetRequest getRequest = new GetRequest("employees", id);
        GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);

        // If document exists, convert it to EmployeeElasticsearch model
        if (getResponse.isExists()) {
            EmployeeElasticsearch employee = new EmployeeElasticsearch(
                    getResponse.getId(),
                    (String) getResponse.getSource().get("name"),
                    (String) getResponse.getSource().get("department"),
                    (double) getResponse.getSource().get("salary")
            );
            return Optional.of(employee);
        } else {
            return Optional.empty(); // Return empty if not found
        }
    }

    // Retrieve all employees from MongoDB
    public List<EmployeeMongo> getAllEmployeesFromMongo() {
        return mongoRepository.findAll(); // Returns all employee documents from MongoDB
    }

    // Delete Employee by ID from MongoDB and Elasticsearch
    public void deleteEmployeeById(String id) throws IOException {
        // Delete from MongoDB
        mongoRepository.deleteById(id);

        // Delete from Elasticsearch
        DeleteRequest deleteRequest = new DeleteRequest("employees", id);
        DeleteResponse deleteResponse = elasticsearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }



}
