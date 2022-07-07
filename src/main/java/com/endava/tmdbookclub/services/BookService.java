package com.endava.tmdbookclub.services;

import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.BookRepository;
import com.endava.tmdbookclub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {
        return bookRepository.findById(id).get();
    }

    public Book addBook(Integer user_id, Book book) {
        if (
                bookRepository.findAll().stream()
                        .filter(b -> b.getTitle().equals(book.getTitle()) && b.getAuthor().equals(book.getAuthor()))
                        .findFirst().isEmpty()
        ) {
            addSingleBook(book);
            return BookGetsOwner(book.getBook_id(), user_id);
        } else {
            Book bookInTable = bookRepository.findAll().stream()
                    .filter(b -> b.getTitle().equals(book.getTitle()) && b.getAuthor().equals(book.getAuthor()))
                    .findFirst().get();

            return BookGetsOwner(bookInTable.getBook_id(), user_id);
        }
    }

    public Book addSingleBook(Book book){
        return bookRepository.saveAndFlush(book);
    }

    public Book BookGetsOwner(Integer book_id, Integer user_id) {
        User user = userRepository.findById(user_id).get();
        Book book = bookRepository.findById(book_id).get();

        book.addOwner(user);

        return bookRepository.saveAndFlush(book);
    }
}
