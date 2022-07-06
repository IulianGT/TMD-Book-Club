package com.endava.tmdbookclub.repositories;

import com.endava.tmdbookclub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
