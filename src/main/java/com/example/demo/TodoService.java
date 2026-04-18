package com.example.demo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class TodoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String dbUrl = System.getenv("FIREBASE_DB_URL");

    private String getAccessToken() throws Exception {
        String credentialsBase64 = System.getenv("FIREBASE_CREDENTIALS");
        byte[] decodedBytes = Base64.getDecoder().decode(credentialsBase64);
        String credentialsJson = new String(decodedBytes, StandardCharsets.UTF_8);

        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8)))
            .createScoped(List.of("https://www.googleapis.com/auth/firebase.database",
                                  "https://www.googleapis.com/auth/userinfo.email"));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }

    private HttpHeaders getHeaders() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public List<Todo> getAllTodos() throws Exception {
        String url = dbUrl + "todos.json";
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Todo> todos = new ArrayList<>();
        if (response.getBody() != null) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) response.getBody()).entrySet()) {
                Todo todo = mapper.convertValue(entry.getValue(), Todo.class);
                todos.add(todo);
            }
        }
        return todos;
    }

    public void addTodo(Todo todo) throws Exception {
        String id = UUID.randomUUID().toString();
        todo.setId(id);
        String url = dbUrl + "todos/" + id + ".json";
        HttpEntity<Todo> entity = new HttpEntity<>(todo, getHeaders());
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    public void updateTodo(String id, Todo todo) throws Exception {
        todo.setId(id);
        String url = dbUrl + "todos/" + id + ".json";
        HttpEntity<Todo> entity = new HttpEntity<>(todo, getHeaders());
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    public void deleteTodo(String id) throws Exception {
        String url = dbUrl + "todos/" + id + ".json";
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }
}