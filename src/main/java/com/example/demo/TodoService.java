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

@Service
public class TodoService {

    // ✅ Don't initialize here — get it lazily inside each method
    private Firestore getDb() {
        return FirestoreClient.getFirestore();
    }

    public List<Todo> getAllTodos() throws Exception {
        ApiFuture<QuerySnapshot> future = getDb().collection("todos").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Todo> todos = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Todo todo = document.toObject(Todo.class);
            if (todo != null) {
                todos.add(todo);
            }
        }
        return todos;
    }

    public void addTodo(Todo todo) throws Exception {
        DocumentReference docRef = getDb().collection("todos").document();
        String id = docRef.getId();
        todo.setId(id);
        docRef.set(todo).get();
    }

    public void updateTodo(String id, Todo todo) throws Exception {
        todo.setId(id);
        getDb().collection("todos").document(id).set(todo).get();
    }

    public void deleteTodo(String id) throws Exception {
        getDb().collection("todos").document(id).delete().get();
    }
}