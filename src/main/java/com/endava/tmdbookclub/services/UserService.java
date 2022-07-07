package com.endava.tmdbookclub.services;

import com.endava.tmdbookclub.Requests.UserRequest;
import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.BookRepository;
import com.endava.tmdbookclub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByID(Integer id) {
        return userRepository.findById(id).get();
    }


    public User createUser(@RequestBody User user) {
        return userRepository.saveAndFlush(user);
    }



}
