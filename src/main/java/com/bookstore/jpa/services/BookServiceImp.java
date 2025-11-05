package com.bookstore.jpa.services;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bookstore.jpa.Mappings.BookMapper;
import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.dtos.Responses.BookResponseDto;
import com.bookstore.jpa.models.AuthorModel;
import com.bookstore.jpa.models.BookModel;
import com.bookstore.jpa.models.ReviewModel;
import com.bookstore.jpa.repositories.AuthorRepository;
import com.bookstore.jpa.repositories.BookRepository;
import com.bookstore.jpa.repositories.PublisherRepository;
import com.bookstore.jpa.services.interfaces.BookService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookServiceImp implements BookService{

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookMapper bookMapper;
    private static final Logger log = LoggerFactory.getLogger(BookServiceImp.class);

    public BookServiceImp(BookRepository bookRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional
    @Override
    public BookResponseDto SaveBook(BookRecordDto bookRecordDto) {
        
        log.info("Inciando tentativa de salvar livroc com titulo: {}", bookRecordDto.title());

        var book = new BookModel();
        book.setTitle(bookRecordDto.title());
        book.setPublisher(publisherRepository.findById(bookRecordDto.publisherId())
        .orElseThrow(()-> new EntityNotFoundException("Editora com o ID" + bookRecordDto.publisherId() +
        "não encontrada.")));

        Set<UUID> authorIds = bookRecordDto.authorIds(); 
        Set<AuthorModel> authors = new HashSet<>(authorRepository.findAllById(authorIds));
        
        if (authors.size() != authorIds.size()){
            throw new EntityNotFoundException("Um ou mais Ids de autores não foram encontrados.");
        }

        book.setAuthors(authors);

        var review = new ReviewModel();
        review.setComment(bookRecordDto.reviewComment());
        review.setBook(book);
        book.setReview(review);

        var savedBook = bookRepository.save(book);

        log.info("Livro salvo com sucesso com o ID: {}", savedBook.getId());
    
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookResponseDto> getAllBooks() {

        log.info("iniciando busca por todos os livros...");

        List<BookModel> books = bookRepository.findAll();

        log.info("Encontrados {} livros.", books.size());

        return books
            .stream()
            .map(bookMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional // Será feita uma deleção em cascata
    @Override
    public void deleteBook(UUID id) {

        log.info("Iniciando tentativa de exclusão do livro com ID: {}", id);

        if(!bookRepository.existsById(id)){
            throw new EntityNotFoundException("Livro com o ID " + id + " não encontrado.");
        }
       
        bookRepository.deleteById(id);

        log.info("Livro com ID: {} excluído com sucesso.", id, id);
    }
}
