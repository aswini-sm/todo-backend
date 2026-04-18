package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Service
public class TodoService {

    private DatabaseReference getDbRef() {
        String dbUrl = System.getenv("FIREBASE_DB_URL");
        return FirebaseDatabase.getInstance(dbUrl).getReference("todos");
    }

    public List<Todo> getAllTodos() throws Exception {
        CompletableFuture<List<Todo>> future = new CompletableFuture<>();
        
        getDbRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Todo> todos = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Todo todo = child.getValue(Todo.class);
                        if (todo != null) todos.add(todo);
                    }
                }
                future.complete(todos);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future.get(10, TimeUnit.SECONDS);
    }

    public void addTodo(Todo todo) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String id = getDbRef().push().getKey();
        todo.setId(id);
        getDbRef().child(id).setValue(todo, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(null);
        });
        future.get(10, TimeUnit.SECONDS);
    }

    public void updateTodo(String id, Todo todo) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        todo.setId(id);
        getDbRef().child(id).setValue(todo, (error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(null);
        });
        future.get(10, TimeUnit.SECONDS);
    }

    public void deleteTodo(String id) throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getDbRef().child(id).removeValue((error, ref) -> {
            if (error != null) future.completeExceptionally(error.toException());
            else future.complete(null);
        });
        future.get(10, TimeUnit.SECONDS);
    }
}