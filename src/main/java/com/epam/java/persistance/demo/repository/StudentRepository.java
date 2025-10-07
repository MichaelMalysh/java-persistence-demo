package com.epam.java.persistance.demo.repository;

import com.epam.java.persistance.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);
}
