package com.epam.java.persistance.demo.controller;


import com.epam.java.persistance.demo.domain.StudentDto;
import com.epam.java.persistance.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;


    @GetMapping
    public List<StudentDto> getAllStudents() {
        return service.getAllStudents();
    }

    @PostMapping
    public StudentDto createStudent(@RequestBody StudentDto studentDto) {
        return service.createStudent(studentDto);
    }

    @GetMapping("/{id}")
    public StudentDto getStudentById(@PathVariable Long id) {
        return service.getStudentById(id);
    }

    @PutMapping("/{id}")
    public StudentDto updateStudent(@PathVariable Long id, @RequestBody StudentDto studentDto) {
        return service.updateStudent(id, studentDto);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
    }
}