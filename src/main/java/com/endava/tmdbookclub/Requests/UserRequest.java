package com.endava.tmdbookclub.Requests;

import com.endava.tmdbookclub.models.Book;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserRequest {
    public Integer user_id;
    public String first_name;
    public String second_name;
    public String username;
    public String password;

    public List<Book> books;

}
