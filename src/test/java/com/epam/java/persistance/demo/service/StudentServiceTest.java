package com.epam.java.persistance.demo.service;

import com.epam.java.persistance.demo.domain.StudentDto;
import com.epam.java.persistance.demo.entity.Student;
import com.epam.java.persistance.demo.exception.DuplicateStudentException;
import com.epam.java.persistance.demo.exception.StudentNotFoundException;
import com.epam.java.persistance.demo.mapper.StudentMapper;
import com.epam.java.persistance.demo.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentServiceImpl service;

    @Test
    void shouldReturnAllStudents() {
        // Mock repository response
        List<Student> students = Arrays.asList(
                new Student(1L, "John", "Doe", "john.doe@example.com", 20),
                new Student(2L, "Jane", "Doe", "jane.doe@example.com", 22)
        );
        when(repository.findAll()).thenReturn(students);

        // Mock mapper response for each student
        when(mapper.toStudentDto(students.get(0))).thenReturn(new StudentDto("John", "Doe", "john.doe@example.com", 20));
        when(mapper.toStudentDto(students.get(1))).thenReturn(new StudentDto("Jane", "Doe", "jane.doe@example.com", 22));

        // Execute service method
        List<StudentDto> result = service.getAllStudents();

        // Assertions
        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("John");
        assertThat(result.get(1).email()).isEqualTo("jane.doe@example.com");

        // Verify repository interactions
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnStudentById() {
        // Mock repository and mapper response
        Student student = new Student(1L, "John", "Doe", "john.doe@example.com", 20);
        when(repository.findById(1L)).thenReturn(Optional.of(student));
        when(mapper.toStudentDto(student)).thenReturn(new StudentDto("John", "Doe", "john.doe@example.com", 20));

        // Execute service method
        StudentDto result = service.getStudentById(1L);

        // Assertions
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.email()).isEqualTo("john.doe@example.com");

        // Verify repository interactions
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFoundById() {
        // Mock repository response
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Assertions
        assertThatThrownBy(() -> service.getStudentById(1L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student with ID 1 not found");

        // Verify repository interactions
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateStudent() {
        // Mock repository and mapper response
        StudentDto studentDto = new StudentDto("John", "Doe", "john.doe@example.com", 20);
        Student student = new Student(null, "John", "Doe", "john.doe@example.com", 20);
        Student savedStudent = new Student(1L, "John", "Doe", "john.doe@example.com", 20);

        when(repository.existsByEmail(studentDto.email())).thenReturn(false);
        when(mapper.toStudentEntity(studentDto)).thenReturn(student);
        when(repository.save(student)).thenReturn(savedStudent);
        when(mapper.toStudentDto(savedStudent)).thenReturn(studentDto);

        // Execute service method
        StudentDto result = service.createStudent(studentDto);

        // Assertions
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        verify(repository, times(1)).existsByEmail(studentDto.email());
        verify(repository, times(1)).save(student);
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateEmailStudent() {
        // Mock repository response
        when(repository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // Assertions
        assertThatThrownBy(() -> service.createStudent(new StudentDto("John", "Doe", "duplicate@example.com", 20)))
                .isInstanceOf(DuplicateStudentException.class)
                .hasMessage("A student with email duplicate@example.com already exists");

        // Verify repository interactions
        verify(repository, times(1)).existsByEmail("duplicate@example.com");
    }

    @Test
    void shouldUpdateStudent() {
        // Mock repository and mapper responses
        Long id = 1L;
        StudentDto studentDto = new StudentDto("Updated", "Doe", "updated.doe@example.com", 25);
        Student existingStudent = new Student(id, "John", "Doe", "john.doe@example.com", 20);
        Student updatedStudent = new Student(id, "Updated", "Doe", "updated.doe@example.com", 25);

        when(repository.findById(id)).thenReturn(Optional.of(existingStudent));
        when(repository.findByEmail(studentDto.email())).thenReturn(Optional.empty());
        when(repository.save(existingStudent)).thenReturn(updatedStudent);
        when(mapper.toStudentDto(updatedStudent)).thenReturn(studentDto);

        // Execute service method
        StudentDto result = service.updateStudent(id, studentDto);

        // Assertions
        assertThat(result.firstName()).isEqualTo("Updated");
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(existingStudent);
    }

    @Test
    void shouldDeleteStudent() {
        // Mock repository response
        when(repository.existsById(1L)).thenReturn(true);

        // Execute service method
        service.deleteStudent(1L);

        // Verify repository interactions
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentStudent() {
        when(repository.existsById(1L)).thenReturn(false);

        // Assertions
        assertThatThrownBy(() -> service.deleteStudent(1L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessage("Student with ID 1 not found");

        verify(repository, times(1)).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
    }
}
