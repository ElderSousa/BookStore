package com.bookstore.jpa.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
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

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
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
        BookResponse result = bookServiceImp.saveBook(bookRecordDto);

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
                bookServiceImp.saveBook(bookRecordDto);
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
                bookServiceImp.saveBook(bookRecordDto);
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

    @Test
    void getBookById_QuandoIdExistente_DeveRetornarBookResponseDto(){
        //Give(Arrage)
        UUID bookId = savedBook.getId();

        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(savedBook));

        when(bookMapper.toDto(savedBook))
            .thenReturn(bookResponseDto);

        //When(Act)
        var bookResponse = bookServiceImp.getBookById(bookId);

        //Then(Assert)
        assertThat(bookResponse).isNotNull();
        assertThat(bookResponse.id()).isEqualTo(bookId);
        assertThat(bookResponse.title()).isEqualTo(savedBook.getTitle());

        verify(bookRepository).findById(bookId);
        verify(bookMapper).toDto(savedBook);
    }

    @Test
    void getBookById_QuandoIdNaoExistente_DeveLancarEntityNotFoundException(){
        //Given(Arrange)
        var idInexistente = UUID.randomUUID();

        when(bookRepository.findById(idInexistente))
            .thenReturn(Optional.empty());

        //When/Then(Act/Assert)
        assertThrows(EntityNotFoundException.class, () -> {
            bookServiceImp.getBookById(idInexistente);
        });

        verify(bookMapper, never()).toDto(any(BookModel.class));
    }

    @Test
    void updateBook_QuandoDtoValido_DeveAtualizarCamposERetornarDto(){
        //Given(Arrange)
        UUID bookId = UUID.randomUUID();
        UUID newPublisherId = UUID.randomUUID();

        var bookUpdateRequest = new BookUpdateRequest(
            "Novo Título",
            newPublisherId,
            "Novo comentário"
        );

        var newPublisher = new PublisherModel();
        newPublisher.setId(newPublisherId);

        var bookOriginal = new BookModel();
        bookOriginal.setId(bookId);
        bookOriginal.setTitle("Titulo antigo");
        bookOriginal.setReview(new ReviewModel());

        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(bookOriginal));

        when(publisherRepository.findById(newPublisherId))
            .thenReturn(Optional.of(newPublisher));

        when(bookRepository.save(any(BookModel.class)))
            .thenReturn(bookOriginal);

        when(bookMapper.toDto(bookOriginal))
            .thenReturn(bookResponseDto);

        //When(Act)
        var bookResponse = bookServiceImp.updateBook(bookId, bookUpdateRequest);

        //Then(Assert)
        assertThat(bookResponse).isNotNull();
        assertThat(bookOriginal.getTitle()).isEqualTo("Novo Título");
        assertThat(bookOriginal.getPublisher()).isEqualTo(newPublisher);
        assertThat(bookOriginal.getReview().getComment()).isEqualTo("Novo comentário");

        verify(bookRepository).save(bookOriginal);
    } 

    @Test
    void addAuthorToBook_QuandoIdsValidos_DeveAdicionarAutor(){
        //Given(Arrange)
        UUID bookId = UUID.randomUUID();
        var authorRequest = new AuthorAssociationRequest(UUID.randomUUID());

        var authorParaAdicionar = new AuthorModel();
        authorParaAdicionar.setId(authorRequest.authorId());

        var book = new BookModel();
        book.setId(bookId);
        book.setAuthors(new HashSet<>());

        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(book));

        when(authorRepository.findById(authorRequest.authorId()))
            .thenReturn(Optional.of(authorParaAdicionar));

        //Whe(Act)
        bookServiceImp.addAuthorToBook(bookId, authorRequest);

        //Then(Assert)
        assertThat(book.getAuthors()).contains(authorParaAdicionar);

        verify(bookRepository).save(book);
             
    }

    @Test
    void addAuthorToBook_QuandoAutorJaAssociado_DeveLancarResourceAlreadyExistsException(){
        //Given(Arrange)
        var bookId = UUID.randomUUID();
        var authorRequest = new AuthorAssociationRequest(UUID.randomUUID());

        var authorInexistente = new AuthorModel();
        authorInexistente.setId(authorRequest.authorId());

        var bookModel = new BookModel();
        bookModel.setId(bookId);
        bookModel.setAuthors(new HashSet<>(Set.of(authorInexistente)));      
        
        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(bookModel));
        
        when(authorRepository.findById(authorRequest.authorId()))
            .thenReturn(Optional.of(authorInexistente));

        //When/Then(Act/Assert)
        assertThrows(ResourceAlreadyExistsException.class, ()-> {
            bookServiceImp.addAuthorToBook(bookId, authorRequest);
        });

        verify(bookRepository, never()).save(any(BookModel.class));
    }

    @Test
    void removerAuthorFromBook_QuandoUnicoAutor_DeveLancarBusinessRuleException(){
        //Given(Arrange)
        var bookId = UUID.randomUUID();
        var author = new AuthorAssociationRequest(UUID.randomUUID());

        var unicoAuthor = new AuthorModel();
        unicoAuthor.setId(author.authorId());

        var book = new BookModel();
        book.setId(bookId);
        book.setAuthors(new HashSet<>(Set.of(unicoAuthor)));

        when(bookRepository.findById(bookId))
            .thenReturn(Optional.of(book));

        when(authorRepository.findById(unicoAuthor.getId()))
            .thenReturn(Optional.of(unicoAuthor));

        //When/Then(Act/Assert)
        assertThrows(BusinessRuleException.class, () ->{
            bookServiceImp.removeAuthorFromBook(bookId, author);
        });

        verify(bookRepository, never()).save(any(BookModel.class));
    }
}
