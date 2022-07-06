package com.endava.tmdbookclub.controllers;

import com.endava.tmdbookclub.Requests.UserRequest;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.UserRepository;
import com.endava.tmdbookclub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("tables/users")
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
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @PostMapping("add_user")
    public User addUser(@RequestBody UserRequest userRequest){
        return userService.addUser(userRequest);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id){
        userService.deleteUserById(id);
    }

}
