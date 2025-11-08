package com.bookstore.jpa.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.jpa.Mappings.BookMapper;
import com.bookstore.jpa.dtos.records.Requests.BookCreateRequest;
import com.bookstore.jpa.dtos.records.Responses.BookResponse;
import com.bookstore.jpa.models.AuthorModel;
import com.bookstore.jpa.models.BookModel;
import com.bookstore.jpa.models.PublisherModel;
import com.bookstore.jpa.repositories.AuthorRepository;
import com.bookstore.jpa.repositories.BookRepository;
import com.bookstore.jpa.repositories.PublisherRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookServiceImpTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImp bookServiceImp;

    private BookCreateRequest bookRecordDto;
    private PublisherModel publisherModel;
    private Set<AuthorModel> authorModels;
    private List<AuthorModel> authorList;
    private BookModel savedBook;
    private BookResponse bookResponseDto;

    @BeforeEach
    void setUp(){

        UUID publisherId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        bookRecordDto = new BookCreateRequest(
            "O Senhor dos Anéis", 
            publisherId, 
            Set.of(authorId), 
            "Ótimo livro"
        );

        publisherModel = new PublisherModel();
        publisherModel.setId(publisherId);
        publisherModel.setName("Editora Teste");

        var author = new AuthorModel();
        author.setId(authorId);
        author.setName("J.R.R. Tolkien");

        authorModels = Set.of(author);

        authorList = new ArrayList<>(authorModels);

        savedBook = new BookModel();
        savedBook.setId(bookId);
        savedBook.setTitle(bookRecordDto.title());
        savedBook.setPublisher(publisherModel);
        savedBook.setAuthors(authorModels);

        bookResponseDto = new BookResponse(
                    bookId, 
                    bookRecordDto.title(), 
                    null,
                    null,
                    null
                );
    }

    @Test
    void SaveBook_QuandoDtoValido_DeveRetornarBookResponseDto(){

        //Given(Arrange)
        when(publisherRepository.findById(bookRecordDto.publisherId()))
            .thenReturn(Optional.of(publisherModel));
        
        when(authorRepository.findAllById(bookRecordDto.authorIds()))
            .thenReturn(authorList);

        when(bookRepository.save(any(BookModel.class)))
            .thenReturn(savedBook);
        
        when(bookMapper.toDto(savedBook))
            .thenReturn(bookResponseDto);

        //When(Act)
        BookResponse result = bookServiceImp.SaveBook(bookRecordDto);

        //Then(Assert)
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedBook.getId());
        assertThat(result.title()).isEqualTo("O Senhor dos Anéis");

        verify(publisherRepository).findById(bookRecordDto.publisherId());
        verify(authorRepository).findAllById(bookRecordDto.authorIds());
        verify(bookRepository).save(any(BookModel.class));
        verify(bookMapper).toDto(savedBook);

    }

    @Test
    void SaveBook_QuandoPublisherIdNaoEncontrado_DeveLancarEntityNotFoundException() {
        // 1. Given (Arrange)
        when(publisherRepository.findById(bookRecordDto.publisherId()))
            .thenReturn(Optional.empty());
        
        //When & 3. Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> {
                bookServiceImp.SaveBook(bookRecordDto);
            }
        );

        assertThat(exception.getMessage()).contains("Editora com o ID" + bookRecordDto.publisherId() +
        "não encontrada.");

        verify(bookRepository, never()).save(any(BookModel.class));

    }

    @Test
    void SaveBook_QuandoAuthorIdNaoEncontrado_DeveLancarEntityNotFoundException() {

        //Given(Arrange)

        when(publisherRepository.findById(bookRecordDto.publisherId()))
            .thenReturn(Optional.of(publisherModel));

        when(authorRepository.findAllById(bookRecordDto.authorIds()))
            .thenReturn(new ArrayList<>());

        //When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () ->{
                bookServiceImp.SaveBook(bookRecordDto);
            }
        );

        assertThat(exception.getMessage()).contains("Um ou mais Ids de autores não foram encontrados.");

        verify(bookRepository, never()).save(any(BookModel.class));
    }
          
    @Test
    void getAllBooks_QuandoExistiremLivros_DeveRetornarListaDeBookResponseDto(){

        //Given(Arrange)
        List<BookModel> bookModels = List.of(savedBook);

        when(bookRepository.findAll())
            .thenReturn(bookModels);

        when(bookMapper.toDto(savedBook))
            .thenReturn(bookResponseDto);

        //When(Act) 
        List<BookResponse> booksResponse = bookServiceImp.getAllBooks();

        //Then(Assert)
        assertThat(booksResponse).isNotNull();
        assertThat(booksResponse).hasSize(1);
        assertThat(booksResponse.get(0).id()).isEqualTo(bookResponseDto.id());    
    }

    @Test 
    void getAllBooks_QuandoNaoExistemLivros_DeveRetornarListaVazia(){

        //Given(Arrange)
        when(bookRepository.findAll())
            .thenReturn(List.of());

        //When(Act)
        List<BookResponse> booksResponse = bookServiceImp.getAllBooks();

        //Then(Assert)
        assertThat(booksResponse).isNotNull();
        assertThat(booksResponse).isEmpty();

        verify(bookMapper, never()).toDto(any(BookModel.class));
    }

    @Test
    void deleteBook_QuandoIdExistente_DeveChamarDeleteById(){

        //Given(Arrange)
        UUID bookId = savedBook.getId();

        when(bookRepository.existsById(bookId))
            .thenReturn(true);

        doNothing().when(bookRepository).deleteById(bookId);

        //When & Then(Act & Assert)
        assertDoesNotThrow(() -> bookServiceImp.deleteBook(bookId));

        verify(bookRepository).existsById(bookId);
        verify(bookRepository).deleteById(bookId);
   
    }

    @Test
    void deleteBook_QuandoIdNaoExistente_DeveLancarEntityNotFoundException(){

        //Given(Arrange)
        UUID bookId = savedBook.getId();

        when(bookRepository.existsById(bookId))
            .thenReturn(false);

        //When & Then(Act & Assert)
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> {
                bookServiceImp.deleteBook(bookId);
            }
        );
      
        assertThat(exception.getMessage()).contains("Livro com o ID " + bookId + " não encontrado.");
    }
}
