package com.bookstore.jpa.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorRepository, UUID> {

}
