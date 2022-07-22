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
        if (bookRepository.findAll().isEmpty())
            throw new ApiRequestException("There are no books registered.");
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id) {

        if (bookRepository.findById(id).isEmpty())
            throw new ApiRequestException("There is no book with this id registered.");
        return bookRepository.findById(id).get();
    }

    public Book addSingleBook(Book book) {
        return bookRepository.saveAndFlush(book);
    }

    public Book findBookByTitleAndAuthor(String title, String author) {
        if (title.isEmpty())
            throw new ApiRequestException("Title must exist");

        if (author.isEmpty())
            throw new ApiRequestException("Title must exist");

        if (bookRepository.findAll()
                .stream().noneMatch(book -> book.getTitle().equals(title)
                        &&
                        book.getAuthor().equals(author))
        )
            return null;

        return bookRepository.findAll()
                .stream().filter(book -> book.getTitle().equals(title)
                        &&
                        book.getAuthor().equals(author)).findFirst().get();
    }

    public Book BookGetsOwner(Integer book_id, Integer user_id) {
        User user = userRepository.findById(user_id).get();
        Book book = bookRepository.findById(book_id).get();

        book.addOwner(user);

        return bookRepository.saveAndFlush(book);
    }

    public Book addBook(Integer user_id, Book book) {
        if (userRepository.findById(user_id).isEmpty())
            throw new ApiRequestException("This user doesn't exist.");

        if (
                findBookByTitleAndAuthor(book.getTitle(), book.getAuthor()) != null
        ) {
            Book bookInTable = findBookByTitleAndAuthor(book.getTitle(), book.getAuthor());
            return BookGetsOwner(bookInTable.getBook_id(), user_id);
        }

        addSingleBook(book);
        return BookGetsOwner(book.getBook_id(), user_id);
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
        int numberOfRentings = (int) rentingService.findAllOnGoingRentings().stream()
                .filter(renting -> renting.getBook_rented().equals(actualBook)).count();
        return numberOfOwners > numberOfRentings;
    }

    public boolean isThisBookAvailableForUser(Integer book_id, Integer user_id) {
        if (userRepository.findById(user_id).isEmpty())
            throw new ApiRequestException("This user doesn't exist");

        if (bookRepository.findById(book_id).isEmpty())
            throw new ApiRequestException("This book doesn't exist.");

        Book actualBook = bookRepository.findById(book_id).get();

        if (actualBook.getUsers().isEmpty())
            throw new ApiRequestException("No owners of this book at all");

        if (rentingRepository.findAll().isEmpty()
                &&
                actualBook.getUsers().stream()
                        .anyMatch(owner -> !owner.getUser_id().equals(user_id)))
            return true;

        int numberOfOwners = (int) actualBook.getUsers().
                stream().filter(owner -> !owner.getUser_id().equals(user_id)).count();

        int numberOfRentings = (int) rentingService.findAllOnGoingRentings().stream()
                .filter(renting -> renting.getBook_rented().equals(actualBook)
                        && !renting.getOwner_id().equals(user_id)).count();
        return numberOfOwners > numberOfRentings;
    }

    public List<Book> findAllByTitle(String title) {
        if (bookRepository.findAll().stream()
                .noneMatch(book -> book.getTitle().equals(title))
        )
            throw new ApiRequestException("There are no books matching the title");

        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title))
                .collect(Collectors.toList());
    }

    public List<Book> findAllByTitleOrAuthor(String title,
                                             String author) {
        if (bookRepository.findAll().stream()
                .noneMatch(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
        )
            throw new ApiRequestException("There are no books matching the title or author");

        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title) || book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Book> findAvailableBooksByTitleOrAuthor(String title, String author) {
        if (bookRepository.findAll().stream()
                .noneMatch(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
        )
            throw new ApiRequestException("There are no books matching the title or author");

        if (bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
                .noneMatch(book -> isThisBookAvailable(book.getBook_id())))
            throw new ApiRequestException("There are no available books with this title or author");

        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().equals(title)
                        ||
                        book.getAuthor().equals(author))
                .filter(book -> isThisBookAvailable(book.getBook_id()))
                .collect(Collectors.toList());
    }
}
