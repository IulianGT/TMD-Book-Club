package com.endava.tmdbookclub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity( name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer book_id;
    private String title;
    private String author;


    @JsonIgnore
    @ManyToMany(mappedBy = "books")
    private List<User> users;

    public void addUser(User user){
        this.users.add(user);
    }
}
