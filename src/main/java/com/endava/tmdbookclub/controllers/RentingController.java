package com.endava.tmdbookclub.controllers;

import com.endava.tmdbookclub.models.Renting;
import com.endava.tmdbookclub.services.RentingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("renting")
public class RentingController {

    @Autowired
    private RentingService rentingService;

    @PutMapping("/{user_id}/Rents/{book_id}_for_{period}")
    public Renting rentABook(
            @PathVariable Integer user_id,
            @PathVariable Integer book_id,
            @PathVariable Integer period) {
        return rentingService.someoneRentsABook(user_id, book_id, period);
    }

    @PutMapping("/{user_id}/extends_renting/{book_id}_for_{period}")
    public Renting extendPeriodOfARentedBook(@PathVariable Integer user_id,
                                             @PathVariable Integer book_id,
                                             @PathVariable Integer period){
        return rentingService.extendPeriodOfARentedBook(user_id, book_id, period);
    }

    @GetMapping
    public List<Renting> findALl() {
        return rentingService.findAll();
    }

    @GetMapping("/on_going")
    public List<Renting> findAllOnGoingRentings(){
        return rentingService.findAllOnGoingRentings();
    }

}

