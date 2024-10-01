package com.example.spring_elastic.service;

import com.example.spring_elastic.model.EmployeeMongo;
import com.example.spring_elastic.repository.EmployeeMongoRepository;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BatchSyncService {

    @Autowired
    private EmployeeMongoRepository mongoRepository;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void syncMongoToElasticsearch() throws IOException {
        List<EmployeeMongo> employees = mongoRepository.findAll();

        for (EmployeeMongo employee : employees) {
            IndexRequest indexRequest = new IndexRequest("employees")
                    .id(employee.getId())
                    .source("name", employee.getName(),
                            "department", employee.getDepartment(),
                            "salary", employee.getSalary());

            elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }
}
