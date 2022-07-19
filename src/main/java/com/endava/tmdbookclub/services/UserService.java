package com.endava.tmdbookclub.services;

import com.endava.tmdbookclub.exceptions.ApiRequestException;
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
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private RentingRepository rentingRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByID(Integer id) {
        return userRepository.findById(id).get();
    }


    public User createUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    public LocalDate whenToReturnSpecifiedBook(Integer user_id, Integer book_id) {
        if (userRepository.findById(user_id).isEmpty())
            throw new ApiRequestException("This user doesn't exist.");

        if (bookRepository.findById(book_id).isEmpty())
            throw new ApiRequestException("This book doesn't exist.");

        if(userRepository.findById(user_id).get().getWhich_books().stream()
                .noneMatch(renting -> renting.getBook_rented().equals(bookRepository.findById(book_id).get())))
            throw new ApiRequestException("This renting doesn't exist");

        return userRepository.findById(user_id).get().getWhich_books().stream()
                .filter(renting -> renting.getBook_rented().equals(bookRepository.findById(book_id).get()))
                .findFirst().get().getWhen_to_return();
    }

    public List<Renting> findAllBooksThatYouHaveBorrowed(Integer user_id){
        if(userRepository.findById(user_id).isEmpty())
            throw new ApiRequestException("This user doesn't exist");
        User user = userRepository.findById(user_id).get();
        if(user.getBooks().isEmpty())
            throw new ApiRequestException("You dont have any books added");
        if(rentingRepository.findAll().stream().filter(renting -> renting.getOwner_id().equals(user.getUser_id())).findFirst().isEmpty())
            throw new ApiRequestException("No books have been borrowed from you");

        return rentingRepository.findAll().stream()
                .filter(renting -> renting.getOwner_id().equals(user.getUser_id()))
                .collect(Collectors.toList());
    }

    public List<Renting> findAllBooksYouHaveToReturn(Integer user_id){
        if(userRepository.findById(user_id).isEmpty())
            throw new ApiRequestException("This user doesn't exist");
        User user = userRepository.findById(user_id).get();
        if(user.getWhich_books().stream()
                .filter(renting -> renting.getWhen_to_return().isAfter(LocalDate.now())).findFirst().isEmpty())
            throw new ApiRequestException("You dont have books that need returned");

        return user.getWhich_books().stream()
                .filter(renting -> renting.getWhen_to_return().isAfter(LocalDate.now()))
                .collect(Collectors.toList());

    }

}
