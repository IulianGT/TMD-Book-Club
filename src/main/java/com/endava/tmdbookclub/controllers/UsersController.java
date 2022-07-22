package com.endava.tmdbookclub.controllers;

import com.endava.tmdbookclub.models.Renting;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getList() {
        return userService.getAllUsers();
    }

    @GetMapping
    @RequestMapping("{id}")
    public User get(@PathVariable Integer id) {
        return userService.getUserByID(id);
    }

    @PostMapping("/create_user")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{user_id}/to_return={book_id}")
    public LocalDate whenToReturnSpecifiedBook(@PathVariable Integer user_id, @PathVariable Integer book_id) {
        return userService.whenToReturnSpecifiedBook(user_id, book_id);
    }

    @GetMapping("/{user_id}/books_rented")
    public List<Renting> findAllTheBooksThatYouHaveBorrowed(@PathVariable Integer user_id) {
        return userService.findAllBooksThatYouHaveRentedToSomeone(user_id);
    }

    @GetMapping("/{user_id}/books_to_return")
    public List<Renting> findAllBooksYouHaveToReturn(@PathVariable Integer user_id) {
        return userService.findAllBooksYouHaveToReturnToSomeone(user_id);
    }
}


