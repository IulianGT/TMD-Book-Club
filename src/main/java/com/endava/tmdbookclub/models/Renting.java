package com.endava.tmdbookclub.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class Renting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer renting_id;

    private Integer owner_id;
    private LocalDate date_of_renting;
    private LocalDate when_to_return;
    private boolean extended;

    @ManyToOne
    @JoinColumn(name = "book_id",referencedColumnName = "book_id")
    @JsonIgnoreProperties({"users","which_renter"})
    private Book book_rented;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "user_id")
    @JsonIgnoreProperties({"username","password","books","which_books"})
    private User who_rented;

}
