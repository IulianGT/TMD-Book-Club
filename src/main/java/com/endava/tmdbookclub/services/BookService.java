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

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id){
        return bookRepository.findById(id).get();
    }

    public Book addBook(Book book){
        return bookRepository.saveAndFlush(book);
    }

    public User addBookToSpecificUser(Book book, Integer id){
        try {
            User temporaryUser = userRepository.findById(id).get();
            List<Book> newListOfBooks = temporaryUser.getBooks();
            newListOfBooks.add(book);
            temporaryUser.setBooks(newListOfBooks);

            return temporaryUser;
        }catch (NullPointerException npe){
            System.out.println("User with specified ID doesn't exist\n");
        }

        return userRepository.findById(id).get();
    }
}
