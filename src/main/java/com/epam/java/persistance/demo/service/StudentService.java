package com.epam.java.persistance.demo.service;

import com.epam.java.persistance.demo.domain.StudentDto;

import java.util.List;

public interface StudentService {

    List<StudentDto> getAllStudents();

    StudentDto getStudentById(Long id);

    StudentDto createStudent(StudentDto studentDto);

    StudentDto updateStudent(Long id, StudentDto studentDto);

    void deleteStudent(Long id);

    List<StudentDto> findStudentsOlderThan(int age);

    List<StudentDto> findStudentsByName(String name);
}
