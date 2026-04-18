package com.example.demo;

import com.google.api.core.ApiFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class TodoService {

    private final DatabaseReference dbRef =
        FirebaseDatabase.getInstance().getReference("todos");

    // GET all todos
    public List<Todo> getAllTodos() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final DataSnapshot[] snapshotHolder = new DataSnapshot[1];
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snapshotHolder[0] = dataSnapshot;
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if needed
                latch.countDown();
            }
        });
        latch.await();
        DataSnapshot snapshot = snapshotHolder[0];
        List<Todo> todos = new ArrayList<>();
        if (snapshot != null && snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                Todo todo = child.getValue(Todo.class);
                if (todo != null) {
                    todos.add(todo);
                }
            }
        }
        return todos;
    }

    // POST - add a new todo
    public void addTodo(Todo todo) throws Exception {
        String id = dbRef.push().getKey();
        todo.setId(id);
        dbRef.child(id).setValueAsync(todo).get();
    }

    // PUT - update an existing todo
    public void updateTodo(String id, Todo todo) throws Exception {
        todo.setId(id);
        dbRef.child(id).setValueAsync(todo).get();
    }

    // DELETE - remove a todo
    public void deleteTodo(String id) throws Exception {
        dbRef.child(id).removeValueAsync().get();
    }
}
