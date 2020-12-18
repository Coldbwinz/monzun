package com.example.monzun.repositories;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StartupRepository extends JpaRepository<Startup, Long>, StartupRepositoryWithJOOQ {
    Optional<Startup> findByName(String name);
    Optional<Startup> findByOwner(User user);
}
