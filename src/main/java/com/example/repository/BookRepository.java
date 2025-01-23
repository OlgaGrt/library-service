package com.example.repository;

import com.example.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findBookByTitleAndAuthor(String title, String author);

    Page<Book> findBooksByPublicationDateBefore(LocalDate localDate, Pageable pageable);

}
