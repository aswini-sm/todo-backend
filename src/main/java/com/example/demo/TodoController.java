package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    // GET all todos
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() throws Exception {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    // POST - add todo
    @PostMapping
    public ResponseEntity<String> addTodo(@RequestBody Todo todo) throws Exception {
        todoService.addTodo(todo);
        return ResponseEntity.ok("Todo added successfully!");
    }

    // PUT - update todo
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTodo(
            @PathVariable String id,
            @RequestBody Todo todo) throws Exception {
        todoService.updateTodo(id, todo);
        return ResponseEntity.ok("Todo updated successfully!");
    }

    // DELETE - delete todo
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable String id) throws Exception {
        todoService.deleteTodo(id);
        return ResponseEntity.ok("Todo deleted successfully!");
    }
}
