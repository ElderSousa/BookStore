package com.bookstore.jpa.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.models.BookModel;
import com.bookstore.jpa.models.ReviewModel;
import com.bookstore.jpa.repositories.AuthorRepository;
import com.bookstore.jpa.repositories.BookRepository;
import com.bookstore.jpa.repositories.PublisherRepository;
import com.bookstore.jpa.services.interfaces.BookService;

import jakarta.transaction.Transactional;

@Service
public class BookServiceImp implements BookService{

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookServiceImp(BookRepository bookRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    @Transactional
    @Override
    public BookModel SaveBook(BookRecordDto bookRecordDto) {
        
        var book = new BookModel();
        book.setTitle(bookRecordDto.title());
        book.setPublisher(publisherRepository.findById(bookRecordDto.publisherId()).get());
        book.setAuthors(authorRepository.findAllById(bookRecordDto.authorIds())
        .stream()
        .collect(Collectors.toSet()));

        var review = new ReviewModel();
        review.setComment(bookRecordDto.reviewComment());
        review.setBook(book);
        book.setReview(review);

        return bookRepository.save(book);
    }

    @Override
    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional // Será feita uma deleção em cascata
    @Override
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }
}
