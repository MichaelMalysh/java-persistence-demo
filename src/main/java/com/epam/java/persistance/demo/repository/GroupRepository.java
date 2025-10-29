package com.epam.java.persistance.demo.repository;

import com.epam.java.persistance.demo.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.students WHERE g.code = :code")
    Optional<Group> findByCode(@Param("code") String code);

    boolean existsByCode(String code);

    @Query("SELECT DISTINCT g FROM Group g LEFT JOIN FETCH g.students WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Group> searchByNameOrCode(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT g FROM Group g LEFT JOIN FETCH g.students WHERE EXISTS " +
            "(SELECT 1 FROM Student s WHERE s MEMBER OF g.students AND s.email = :email)")
    List<Group> findGroupsByStudentEmail(@Param("email") String email);

    @Query("SELECT g.id FROM Group g LEFT JOIN g.students s GROUP BY g.id ORDER BY COUNT(s) DESC")
    List<Long> findGroupIdsOrderedByStudentCount();

}
