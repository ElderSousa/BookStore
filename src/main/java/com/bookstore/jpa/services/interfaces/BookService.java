package com.bookstore.jpa.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.models.BookModel;

public interface BookService {

    BookModel SaveBook(BookRecordDto bookRecordDto);
    List<BookModel> getAllBooks();
    void deleteBook(UUID id);
}
