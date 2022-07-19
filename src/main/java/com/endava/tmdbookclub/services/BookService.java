package com.endava.tmdbookclub.services;

import com.endava.tmdbookclub.exceptions.ApiRequestException;
import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.BookRepository;
import com.endava.tmdbookclub.repositories.RentingRepository;
import com.endava.tmdbookclub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentingRepository rentingRepository;

    @Autowired
    private RentingService rentingService;

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

    public Book addSingleBook(Book book) {
        return bookRepository.saveAndFlush(book);
    }

    public Book BookGetsOwner(Integer book_id, Integer user_id) {
        User user = userRepository.findById(user_id).get();
        Book book = bookRepository.findById(book_id).get();

        book.addOwner(user);

        return bookRepository.saveAndFlush(book);
    }

    public boolean isThisBookAvailable(Integer book_id) {
        if (bookRepository.findById(book_id).isEmpty())
            throw new ApiRequestException("This book doesn't exist.");

        Book actualBook = bookRepository.findById(book_id).get();

        if (actualBook.getUsers().isEmpty())
            throw new ApiRequestException("No owners of this book at all");

        if (rentingRepository.findAll().isEmpty())
            return true;

        int numberOfOwners = actualBook.getUsers().size();
        int numberOfRentings = (int)rentingService.findAllOnGoingRentings().stream()
                .filter(renting -> renting.getBook_rented().equals(actualBook)).count();
        return numberOfOwners>numberOfRentings;
    }

    public List<Book> findAllByTitleOrAuthor( String title,
                                             String author){
        if(bookRepository.findAll().stream()
                .noneMatch(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
        )
            throw new ApiRequestException("There are no books matching the title or author");

        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title) || book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Book> findAllByTitle( String title){
        if(bookRepository.findAll().stream()
                .noneMatch(book -> book.getTitle().equals(title))
        )
            throw new ApiRequestException("There are no books matching the title");

        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title))
                .collect(Collectors.toList());
    }

    public List<Book> findAvailableBooksByTitleOrAuthor(String title, String author) {
        if (bookRepository.findAll().stream()
                .noneMatch(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
        )
            throw new ApiRequestException("There are no books matching the title or author");

        if(bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
                .noneMatch(book -> isThisBookAvailable(book.getBook_id())))
            throw new ApiRequestException("there are no available books with this title or author");

        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
                .filter(book -> isThisBookAvailable(book.getBook_id()))
                .collect(Collectors.toList());
    }
}
