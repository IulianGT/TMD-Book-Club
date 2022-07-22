package com.endava.tmdbookclub.services;

import com.endava.tmdbookclub.exceptions.ApiRequestException;
import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.models.Renting;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.BookRepository;
import com.endava.tmdbookclub.repositories.RentingRepository;
import com.endava.tmdbookclub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentingService {

    @Autowired
    private RentingRepository rentingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    public List<Renting> findAll() {
        if (rentingRepository.findAll().isEmpty())
            throw new ApiRequestException("There are no rentings yet");

        return rentingRepository.findAll();
    }

    public List<Renting> findAllOnGoingRentings() {
        if (rentingRepository.findAll().isEmpty())
            throw new ApiRequestException("There are no rentings yet");

        return rentingRepository.findAll().stream().filter(renting -> renting.getWhen_to_return().isAfter(LocalDate.now())).collect(Collectors.toList());
    }


    public Renting someoneRentsABook(Integer user_id, Integer book_id, Integer period) {
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ApiRequestException("The user does not exist.");
        }
        User actualUser = userRepository.findById(user_id).get();

        if (bookRepository.findById(book_id).isEmpty()) {
            throw new ApiRequestException("This book does not exist");
        }
        Book actualBook = bookRepository.findById(book_id).get();

        if (getOnGoingRentingByBookAndRenter(book_id, user_id) != null) {
            throw new ApiRequestException("This renting is still going. You may have extended");
        }

        Renting renting = new Renting();
        renting.setDate_of_renting(LocalDate.now());

        if (period > 4 || period < 1) {
            throw new ApiRequestException("Not a valid period to chose from.");
        }

        if (period == 4) {
            renting.setWhen_to_return(
                    LocalDate.now().plusMonths(1));
        } else {
            renting.setWhen_to_return(
                    LocalDate.now().plusWeeks(period)
            );
        }

        renting.setBook_rented(actualBook);
        renting.setWho_rented(actualUser);

        if (!bookService.isThisBookAvailableForUser(book_id,user_id))
                throw new ApiRequestException("There are no owners of this book available");

        renting.setOwner_id(
                        actualBook.getUsers().stream()
                                .filter( owner -> !owner.getUser_id().equals(user_id))
                                .findFirst().get().getUser_id()
                );

        return rentingRepository.saveAndFlush(renting);
    }

    public Renting getOnGoingRentingByBookAndRenter(Integer book_id, Integer user_id) {
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ApiRequestException("The user does not exist.");
        }
        User actualRenter = userRepository.findById(user_id).get();

        if (bookRepository.findById(book_id).isEmpty()) {
            throw new ApiRequestException("This book does not exist");
        }
        Book actualBook = bookRepository.findById(book_id).get();

        if(rentingRepository.findAll().isEmpty())
            return null;

        Renting actualRenting;
        if (findAllOnGoingRentings().stream()
                .anyMatch(renting ->
                        renting.getWho_rented().equals(actualRenter)
                                &&
                                renting.getBook_rented().equals(actualBook)
                )) {
            return actualRenting = findAllOnGoingRentings().stream()
                    .filter(renting ->
                            renting.getWho_rented().equals(actualRenter)
                                    &&
                                    renting.getBook_rented().equals(actualBook)
                    ).findFirst().get();
        }

        return null;
    }

    public Renting extendPeriodOfARentedBook(Integer user_id, Integer book_id, Integer period) {
        User actualUser;
        Book actualBook;
        if (userRepository.findById(user_id).isEmpty())
            throw new ApiRequestException("The user does not exist.");

        actualUser = userRepository.findById(user_id).get();

        if (bookRepository.findById(book_id).isEmpty())
            throw new ApiRequestException("This book does not exist");

        actualBook = bookRepository.findById(book_id).get();


        Renting actualRenting = null;
        if (actualBook.getWhich_renter().stream()
                .noneMatch(r -> r.getWho_rented().equals(actualUser))) {
            throw new ApiRequestException("No such renting was made.");
        }

        //retin rentingul corespunzator userului si cartii date ca si parametru
        actualRenting = actualBook.getWhich_renter().stream()
                .filter(r -> r.getWho_rented().equals(actualUser)).findFirst().get();

        if (actualRenting.isExtended()) {
            throw new ApiRequestException("You can not extend this renting anymore.");
        }

        if (period > 2 || period < 1) {
            throw new ApiRequestException("That is not a valid period of extension.");
        }
        actualRenting.setWhen_to_return(actualRenting.getWhen_to_return().plusWeeks(period));
        actualRenting.setExtended(true);

        return rentingRepository.saveAndFlush(actualRenting);
    }


}
