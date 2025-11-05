package com.bookstore.jpa.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.dtos.Responses.BookResponseDto;
import com.bookstore.jpa.services.interfaces.BookService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;


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
    public ResponseEntity<BookResponseDto> saveBook(@RequestBody @Valid BookRecordDto bookRecordDto) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.SaveBook(bookRecordDto));
    }

    @Operation(summary = "Lista todos os livros", 
           description = "Devolve uma lista com todos os livros registados no banco de dados.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de livros devolvida com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
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
    
}
