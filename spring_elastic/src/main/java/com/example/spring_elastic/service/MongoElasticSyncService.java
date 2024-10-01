package com.example.spring_elastic.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Consumer;

@Service
public class MongoElasticSyncService {

    private static final Logger logger = LoggerFactory.getLogger(MongoElasticSyncService.class);

    private final RestHighLevelClient elasticsearchClient;
    private final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27018");
    private final MongoDatabase database = mongoClient.getDatabase("employees");

    @Autowired
    public MongoElasticSyncService(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void syncMongoToElastic() {
        MongoCollection<Document> collection = database.getCollection("employees");

        // Use a Consumer instead of deprecated Block
        Consumer<ChangeStreamDocument<Document>> changeConsumer = (ChangeStreamDocument<Document> change) -> {
            String documentId = change.getDocumentKey() != null ? change.getDocumentKey().get("_id").toString() : null;

            switch (change.getOperationType()) {
                case INSERT:
                case UPDATE:
                    Document fullDocument = change.getFullDocument() != null ? change.getFullDocument() : new Document();
                    String name = fullDocument.containsKey("name") ? fullDocument.getString("name") : "Unknown";
                    String department = fullDocument.containsKey("department") ? fullDocument.getString("department") : "Unknown";
                    double salary = fullDocument.containsKey("salary") ? fullDocument.getDouble("salary") : 0.0;

                    IndexRequest indexRequest = new IndexRequest("employees")
                            .id(documentId)
                            .source("name", name, "department", department, "salary", salary);
                    try {
                        elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        logger.error("Error syncing document with Elasticsearch: {}", documentId, e);
                    }
                    break;

                case DELETE:
                    DeleteRequest deleteRequest = new DeleteRequest("employees", documentId);
                    try {
                        elasticsearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        logger.error("Error deleting document from Elasticsearch: {}", documentId, e);
                    }
                    break;

                default:
                    break;
            }
        };

        // Pass the Consumer to the forEach method
        collection.watch().forEach(changeConsumer);
    }
}
