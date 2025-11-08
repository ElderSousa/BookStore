package com.bookstore.jpa.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.bookstore.jpa.dtos.records.Requests.AuthorAssociationRequest;
import com.bookstore.jpa.dtos.records.Requests.BookRequest.BookCreateRequest;
import com.bookstore.jpa.dtos.records.Requests.BookRequest.BookUpdateRequest;
import com.bookstore.jpa.dtos.records.Responses.BookResponse;

public interface BookService {

    BookResponse saveBook(BookCreateRequest bookRecordDto);
    List<BookResponse> getAllBooks();
    BookResponse getBookById(UUID id);
    BookResponse updateBook(UUID id, BookUpdateRequest bookRequest);
    void deleteBook(UUID id);
    void removeAuthorFromBook(UUID bookId, AuthorAssociationRequest authorId);
    void addAuthorToBook(UUID bookId, AuthorAssociationRequest authorId);
}
