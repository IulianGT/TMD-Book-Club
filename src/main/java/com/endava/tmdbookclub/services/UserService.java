package com.endava.tmdbookclub.services;

import com.endava.tmdbookclub.Requests.UserRequest;
import com.endava.tmdbookclub.models.Book;
import com.endava.tmdbookclub.models.User;
import com.endava.tmdbookclub.repositories.BookRepository;
import com.endava.tmdbookclub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserByID(Integer id){
        return userRepository.findById(id).get();
    }


    public User createUser(@RequestBody User user){
        return userRepository.saveAndFlush(user);
    }
    public User addUser(UserRequest userRequest){
        User user = new User();
        user.setUser_id(userRequest.user_id);
        user.setFirst_name(userRequest.first_name);
        user.setSecond_name(userRequest.second_name);
        user.setUsername(userRequest.username);
        user.setPassword(userRequest.password);
        user.setBooks(userRequest.books.stream()
                .map(book -> {
                    Book bbook = book;
                    if(bbook.getBook_id() > 0){
                        bbook = bookRepository.findById(bbook.getBook_id()).get();
                    }
                    bbook.addUser(user);
                    return bbook;
                }).collect(Collectors.toList()));

        return userRepository.saveAndFlush(user);
    }

    public void deleteUserById(Integer id){
        userRepository.deleteById(id);
    }
}
