package com.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "books")
@NoArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    private UUID id = UUID.randomUUID();

    private String title;
    private String author;
    private LocalDate publicationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private Subscription subscription;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
}