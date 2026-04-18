package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.concurrent.ExecutionException;

@Service
public class TodoService {

    // ✅ Don't initialize here — get it lazily inside each method
    private Firestore getDb() {
        return FirestoreClient.getFirestore();
    }

    private static final String COLLECTION_NAME = "todos";

    public List<Todo> getAllTodos() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDb().collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Todo> todos = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Todo todo = document.toObject(Todo.class);
            todos.add(todo);
        }
        return todos;
    }

    public void addTodo(Todo todo) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(COLLECTION_NAME).document();
        String id = docRef.getId();
        todo.setId(id);
        docRef.set(todo).get();
    }

    public void updateTodo(String id, Todo todo) throws ExecutionException, InterruptedException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        todo.setId(id);
        getDb().collection(COLLECTION_NAME).document(id).set(todo).get();
    }

    public void deleteTodo(String id) throws ExecutionException, InterruptedException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        getDb().collection(COLLECTION_NAME).document(id).delete().get();
    }
}