package com.endava.tmdbookclub.controllers;

import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }


}
