package com.example.bloodmanagement.repository;

import com.example.bloodmanagement.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByUsername(String name);
    Optional<Users> findByEmail(String name);

}
