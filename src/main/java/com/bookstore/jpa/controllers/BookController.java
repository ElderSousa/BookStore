package com.bookstore.jpa.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.models.BookModel;
import com.bookstore.jpa.services.BookServiceImp;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("bookstore/books")
public class BookController {

    private final BookServiceImp bookService;

    public BookController(BookServiceImp bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookModel> saveBook(@RequestBody BookRecordDto bookRecordDto) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.SaveBook(bookRecordDto));
    }

    @GetMapping
    public ResponseEntity<List<BookModel>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable UUID id){
        
        bookService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.OK).body("Book deleted successfully.");
    }
    
}
