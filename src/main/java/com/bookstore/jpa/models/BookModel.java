package com.bookstore.jpa.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_BOOK")
@Getter
@Setter
public class BookModel implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private PublisherModel publisher;

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany //(fetch = FetchType.LAZY)
    @JoinTable(
        name = "TB_BOOK_AUTHOR", //Tabela de associação
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<AuthorModel> authors = new HashSet<>();

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private ReviewModel review;
        
}
