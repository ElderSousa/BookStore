package com.bookstore.jpa.dtos.records.Requests;

public record ReviewRequest() {

    public record ReviewCreateRequest(
        String comment
    ){

    }

    public record ReviewUpdateRequest(
        String comment      
    ){

    }
}
