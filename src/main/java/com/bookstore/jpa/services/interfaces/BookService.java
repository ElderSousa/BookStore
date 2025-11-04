package com.bookstore.jpa.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.dtos.Responses.BookResponseDto;

public interface BookService {

    BookResponseDto SaveBook(BookRecordDto bookRecordDto);
    List<BookResponseDto> getAllBooks();
    void deleteBook(UUID id);
}
