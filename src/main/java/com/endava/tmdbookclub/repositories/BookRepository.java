package com.endava.tmdbookclub.repositories;

import com.endava.tmdbookclub.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Integer> {

}
