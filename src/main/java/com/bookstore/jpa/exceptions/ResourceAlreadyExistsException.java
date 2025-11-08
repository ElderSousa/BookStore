package com.bookstore.jpa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends AppException{
    public ResourceAlreadyExistsException(String message){
        super(message);
    }
}
