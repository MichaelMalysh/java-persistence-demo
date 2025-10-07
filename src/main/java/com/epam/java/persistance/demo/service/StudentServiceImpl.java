package com.epam.java.persistance.demo.service;

import com.epam.java.persistance.demo.domain.StudentDto;
import com.epam.java.persistance.demo.entity.Student;
import com.epam.java.persistance.demo.exception.DuplicateStudentException;
import com.epam.java.persistance.demo.exception.StudentNotFoundException;
import com.epam.java.persistance.demo.mapper.StudentMapper;
import com.epam.java.persistance.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    @Override
    public List<StudentDto> getAllStudents() {
        return repository.findAll()
                .stream()
                .map(mapper::toStudentDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto getStudentById(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));
        return mapper.toStudentDto(student);
    }

    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        if (repository.existsByEmail(studentDto.email())) {
            throw new DuplicateStudentException("A student with email " + studentDto.email() + " already exists");
        }

        Student student = mapper.toStudentEntity(studentDto);
        Student savedStudent = repository.save(student);
        return mapper.toStudentDto(savedStudent);
    }

    @Override
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        Student existingStudent = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        repository.findByEmail(studentDto.email())
                .ifPresent(studentWithSameEmail -> {
                    if (!studentWithSameEmail.getId().equals(id)) {
                        throw new DuplicateStudentException("A student with email " + studentDto.email() + " already exists");
                    }
                });

        existingStudent.setFirstName(studentDto.firstName());
        existingStudent.setLastName(studentDto.lastName());
        existingStudent.setEmail(studentDto.email());
        existingStudent.setAge(studentDto.age());

        Student savedStudent = repository.save(existingStudent);
        return mapper.toStudentDto(savedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!repository.existsById(id)) {
            throw new StudentNotFoundException("Student with ID " + id + " not found");
        }

        repository.deleteById(id);
    }
}
