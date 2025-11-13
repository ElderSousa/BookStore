package com.bookstore.jpa.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.jpa.dtos.records.Requests.PublisherResquest.PublisherCreateRequest;
import com.bookstore.jpa.dtos.records.Responses.PublisherResponse;
import com.bookstore.jpa.services.interfaces.PublisherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("bookstore/publisher")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService){
        this.publisherService = publisherService;
    }

   @Operation(summary = "Cria uma nova editora", 
               description = "Regista uma nova editora no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Editora criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: nome em branco)", 
                     content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @PostMapping
    public ResponseEntity<PublisherResponse> savePublisher(@RequestBody @Valid PublisherCreateRequest publisherRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(publisherService.savePublisher(publisherRequest));
    }
}
