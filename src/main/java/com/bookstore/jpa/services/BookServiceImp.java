package com.bookstore.jpa.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bookstore.jpa.Mappings.interfaces.BookMapper;
import com.bookstore.jpa.dtos.records.Requests.AuthorAssociationRequest;
import com.bookstore.jpa.dtos.records.Requests.BookRequest.BookCreateRequest;
import com.bookstore.jpa.dtos.records.Requests.BookRequest.BookUpdateRequest;
import com.bookstore.jpa.dtos.records.Responses.BookResponse;
import com.bookstore.jpa.exceptions.BusinessRuleException;
import com.bookstore.jpa.exceptions.ResourceAlreadyExistsException;
import com.bookstore.jpa.models.AuthorModel;
import com.bookstore.jpa.models.BookModel;
import com.bookstore.jpa.models.PublisherModel;
import com.bookstore.jpa.models.ReviewModel;
import com.bookstore.jpa.repositories.AuthorRepository;
import com.bookstore.jpa.repositories.BookRepository;
import com.bookstore.jpa.repositories.PublisherRepository;
import com.bookstore.jpa.services.interfaces.BookService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@SuppressWarnings("null")
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
    public BookResponse saveBook(BookCreateRequest bookRequest) {
        
        log.info("Inciando tentativa de salvar livroc com titulo: {}", bookRequest.title());

        var book = new BookModel();
        book.setTitle(bookRequest.title());
        book.setPublisher(publisherRepository.findById(bookRequest.publisherId())
        .orElseThrow(()-> new EntityNotFoundException("Editora com o ID: " + bookRequest.publisherId() +
        " não encontrada.")));

        Set<UUID> authorIds = bookRequest.authorIds(); 
        Set<AuthorModel> authors = new HashSet<>(authorRepository.findAllById(authorIds));
        
        if (authors.size() != authorIds.size()){
            throw new EntityNotFoundException("Um ou mais Ids de autores não foram encontrados.");
        }

        book.setAuthors(authors);

        var review = new ReviewModel();
        review.setComment(bookRequest.reviewComment());
        review.setBook(book);
        book.setReview(review);

        var savedBook = bookRepository.save(book);

        log.info("Livro salvo com sucesso com o ID: {}", savedBook.getId());
        
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookResponse> getAllBooks() {

        log.info("Iniciando busca por todos os livros...");

        List<BookModel> books = bookRepository.findAll();

        log.info("Encontrados {} livros.", books.size());

        return books
            .stream()
            .map(bookMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookById(UUID id){
        log.info("Iniciando busca por Livro com ID: {}", id);

        var book = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Livro com o ID: " + id + " não encontrado."));
        
        log.info("Encontrado Livro com ID: {}", id);
        
        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public BookResponse updateBook(UUID id, BookUpdateRequest bookRequest) {
        
        log.info("Iniciando tentativa de atualização do livro com ID: {}", id);
        BookModel book = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Livro com o ID: " + id + " não encontrado."));
        
        if (bookRequest.title() != null && !bookRequest.title().isBlank()) {
            book.setTitle(bookRequest.title());
        }

        if (bookRequest.publisherId() != null) {
            PublisherModel publisher = publisherRepository.findById(bookRequest.publisherId())
                .orElseThrow(() -> new EntityNotFoundException("Editora com o ID: " + bookRequest.publisherId() + " não encontrada."));
            book.setPublisher(publisher);
        }

        if (bookRequest.reviewComment() != null && !bookRequest.reviewComment().isBlank()) {
            book.getReview().setComment(bookRequest.reviewComment());
        }  

        var updatedBook = bookRepository.save(book);
        log.info("Livro com ID: {} atualizado com sucesso.", id);
        return bookMapper.toDto(updatedBook);
        
    }

    @Transactional // Será feita uma deleção em cascata
    @Override
    public void deleteBook(UUID id) {

        log.info("Iniciando tentativa de exclusão do livro com ID: {}", id);

        if(!bookRepository.existsById(id)){
            throw new EntityNotFoundException("Livro com o ID: " + id + " não encontrado.");
        }
       
        bookRepository.deleteById(id);

        log.info("Livro com ID: {} excluído com sucesso.", id, id);
    }

    @Transactional
    @Override
    public void removeAuthorFromBook(UUID bookId, AuthorAssociationRequest authorAssociation){
        log.info("Iniciando tentativa de remoção do autor com ID: {} do livro com ID: {}", authorAssociation.authorId(), bookId);

        var book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Livro com o ID: " + bookId + " não encontrado."));
        
        var author = authorRepository.findById(authorAssociation.authorId())
            .orElseThrow(() -> new EntityNotFoundException("Autor com o ID: " + authorAssociation.authorId() + " não encontrado."));

        if (book.getAuthors().size() <=1){
            throw new BusinessRuleException("Um livro deve ter pelo menos um autor associado.");
        }

        if (book.getAuthors().remove(author)) {
            bookRepository.save(book);
            log.info("Autor removido com sucesso.");
        } else {
            log.warn("Autor {} não estava associado ao livro {}.", authorAssociation.authorId(), bookId);
        }

    }
    
    @Transactional
    @Override
    public void addAuthorToBook(UUID bookId, AuthorAssociationRequest authorAssociation){
        log.info("Iniciando a tentativa de adicionar o author de ID: {} no livro de ID: {}", bookId, authorAssociation.authorId());

        var book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Livro com o ID: " + bookId + " não encontrado."));

        var author = authorRepository.findById(authorAssociation.authorId())
            .orElseThrow(() -> new EntityNotFoundException("Autor com o ID: " + authorAssociation.authorId() + " não encontrado."));
        
        if (book.getAuthors().contains(author)){
            log.warn("Author do ID: {} já está associado ao Livro do ID: {}", authorAssociation.authorId(), bookId );
            throw new ResourceAlreadyExistsException("Author do ID: " + authorAssociation.authorId() + "ja está associado ao Livro do ID: " + bookId + "");
        }

        book.getAuthors().add(author);
        bookRepository.save(book);
        
        log.info("Autor adicionado com sucesso ao livro.");
    }
}
