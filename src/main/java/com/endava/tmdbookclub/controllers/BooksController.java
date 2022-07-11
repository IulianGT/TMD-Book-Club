package com.endava.tmdbookclub.controllers;

import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/books")
public class BooksController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAll() {
        return bookService.getAllBooks();}

    @GetMapping
    @RequestMapping("{id}")
    public Book getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);}

    @PostMapping("/add")
    public Book addSingleBook(@RequestBody Book book){
        return bookService.addSingleBook(book);
    }
    @PostMapping("/{id}/add")
    public Book addBook(@PathVariable Integer id,@RequestBody Book book) {
        return bookService.addBook(id,book);
    }

    @PutMapping("/{book_id}/user/{user_id}")
    public Book BookGetsOwner(@PathVariable Integer book_id, @PathVariable Integer user_id) {
        return bookService.BookGetsOwner(book_id, user_id);
    }

}
