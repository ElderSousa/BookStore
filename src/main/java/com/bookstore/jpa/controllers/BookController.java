package com.bookstore.jpa.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.jpa.dtos.records.Requests.AuthorAssociationRequest;
import com.bookstore.jpa.dtos.records.Requests.BookRequest.BookCreateRequest;
import com.bookstore.jpa.dtos.records.Requests.BookRequest.BookUpdateRequest;
import com.bookstore.jpa.dtos.records.Responses.BookResponse;
import com.bookstore.jpa.services.interfaces.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;


@RestController
@RequestMapping("bookstore/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Cria um novo livro", 
           description = "Regista um novo livro, associando-o a uma editora (publisher) e a um ou mais autores.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: título em branco, ID nulo)", 
                 content = @Content(schema = @Schema(implementation = Map.class))),
    @ApiResponse(responseCode = "404", description = "Editora (Publisher) ou Autor não encontrado(s) no banco de dados", 
                 content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<BookResponse> saveBook(@RequestBody @Valid BookCreateRequest bookRecordDto) {    
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.saveBook(bookRecordDto));
    }

    @Operation(summary = "Lista todos os livros", 
           description = "Devolve uma lista com todos os livros registados no banco de dados.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de livros devolvida com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {       
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @Operation(summary = "Busca um livro por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado com o ID fornecido", 
                     content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookById(id));
    }

    @Operation(summary = "Atualiza parcialmente um livro",
               description = "Atualiza o 'title', 'publisherId' ou 'reviewComment' de um livro.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Livro ou nova Editora não encontrada", 
                     content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID id, @RequestBody BookUpdateRequest bookUpdateRequest){
        var bookResponse = bookService.updateBook(id, bookUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
    }

    @Operation(summary = "Exclui um livro por ID", 
           description = "Remove um livro do banco de dados com base no seu UUID.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Livro excluído com sucesso"),
    @ApiResponse(responseCode = "404", description = "Livro não encontrado com o ID fornecido", 
                 content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable UUID id){      
        bookService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.OK).body("Book deleted successfully.");
    }
    
    @Operation(summary = "Adiciona um autor a um livro", 
               description = "Associa um autor existente a um livro existente (relação N-para-N).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autor associado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Livro ou Autor não encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflito: Autor já associado a este livro",
                     content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/{bookId}/authors")
    public ResponseEntity<Void> addAuthorToBook(@PathVariable UUID bookId, @Valid @RequestBody AuthorAssociationRequest authorAssociation){
        bookService.addAuthorToBook(bookId, authorAssociation);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove um autor de um livro", 
               description = "Remove a associação de um autor de um livro (relação N-para-N).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autor desassociado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de regra de negócio (Ex: Livro não pode ficar sem autores)",
                     content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Livro ou Autor não encontrado")
    })
    @DeleteMapping("/{bookId}/author")
    public ResponseEntity<Void> removeAuthorFromBook(
            @PathVariable UUID bookId, 
            @RequestBody AuthorAssociationRequest author) {       
        bookService.removeAuthorFromBook(bookId, author);
        return ResponseEntity.ok().build();
    }
}
