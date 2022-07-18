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

@Service
public class RentingService {

    @Autowired
    private RentingRepository rentingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Renting> findAll() {
        if (rentingRepository.findAll().isEmpty())
            throw new ApiRequestException("There are no rentings yet");
        return rentingRepository.findAll();
    }

    public Renting getRentingByBookAndUser(Integer book_id, Integer user_id) {
        User actualUser;
        Book actualBook;
        Renting actualRenting;
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ApiRequestException("The user does not exist.");
        }
        actualUser = userRepository.findById(user_id).get();

        if (bookRepository.findById(book_id).isEmpty()) {
            throw new ApiRequestException("This book does not exist");
        }
        actualBook = bookRepository.findById(book_id).get();

        if (rentingRepository.findAll().stream()
                .anyMatch(renting ->
                        renting.getWho_rented().equals(actualUser)
                                &&
                                renting.getBook_rented().equals(actualBook)
                                &&
                                LocalDate.now().isBefore(renting.getWhen_to_return())
                )) {
            return actualRenting = rentingRepository.findAll().stream()
                    .filter(renting ->
                            renting.getWho_rented().equals(actualUser)
                                    &&
                                    renting.getBook_rented().equals(actualBook)
                                    &&
                                    LocalDate.now().isBefore(renting.getWhen_to_return())
                    ).findFirst().get();
        }

        return null;
    }

    public Renting someoneRentsABook(Integer user_id, Integer book_id, Integer period) {
        User actualUser;
        Book actualBook;
        if (userRepository.findById(user_id).isEmpty()) {
            throw new ApiRequestException("The user does not exist.");
        }
        actualUser = userRepository.findById(user_id).get();

        if (bookRepository.findById(book_id).isEmpty()) {
            throw new ApiRequestException("This book does not exist");
        }
        actualBook = bookRepository.findById(book_id).get();

        if (getRentingByBookAndUser(book_id, user_id) != null) {
            throw new ApiRequestException("This renting is still going. You may have extended");
        }

        Renting renting = new Renting();
        renting.setDate_of_renting(LocalDate.now());

        if (period > 4 || period < 1) {
            throw new ApiRequestException("Not a valid period to chose from.");
        } else if (period == 4) {
            renting.setWhen_to_return(
                    LocalDate.now().plusMonths(1));
        } else {
            renting.setWhen_to_return(
                    LocalDate.now().plusWeeks(period)
            );
        }

        renting.setBook_rented(actualBook);
        renting.setWho_rented(actualUser);

        if (rentingRepository.findAll().isEmpty()) {
            if (actualBook.getUsers().stream()
                    .anyMatch(user -> !user.equals(actualUser)))
                renting.setOwner_id(
                        actualBook.getUsers().stream().findFirst().get().getUser_id()
                );
            else {
                throw new ApiRequestException("you can't rent your own book");
            }
        } else if (actualBook.getUsers().stream()
                .noneMatch(u -> !u.equals(actualUser) &&
                        rentingRepository.findAll().stream()
                                .anyMatch(r -> !r.getOwner_id().equals(u.getUser_id())))
        )
        {
            throw new ApiRequestException("There are no owners of this book available.");
        } else {
            renting.setOwner_id(
                    actualBook.getUsers().stream()
                            .filter(u -> rentingRepository.findAll().stream()
                                    .anyMatch(r -> !r.getOwner_id().equals(u.getUser_id()))
                            ).findFirst().get().getUser_id());
        }
        return rentingRepository.saveAndFlush(renting);
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
