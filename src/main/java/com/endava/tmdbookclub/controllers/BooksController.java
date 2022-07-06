package com.endava.tmdbookclub.controllers;

import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.BookRepository;
import com.endava.tmdbookclub.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tables/books")
public class BooksController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAll(){

        return bookService.getAllBooks();
    }

    @GetMapping
    @RequestMapping("{id}")
    public Book getBookById(@PathVariable Integer id){

        return bookService.getBookById(id);
    }

    @PostMapping("/add")
    public Book addBook(@RequestBody Book book){
        return bookService.addBook(book);
    }

    @PostMapping("/add_books/{id}")
    public User addBookToTheUser(@RequestBody Book book, @PathVariable Integer id){
        return bookService.addBookToSpecificUser(book,id);
    }
}
