package com.epam.java.persistance.demo.controller;

import com.epam.java.persistance.demo.domain.StudentDto;
import com.epam.java.persistance.demo.exception.DuplicateStudentException;
import com.epam.java.persistance.demo.exception.StudentNotFoundException;
import com.epam.java.persistance.demo.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService service;

    private StudentDto studentDto;

    @BeforeEach
    void setUp() {
        studentDto = new StudentDto("John", "Doe", "john.doe@example.com", 20);
    }

    @Test
    void shouldReturnListOfStudents() throws Exception {
        //given
        List<StudentDto> students = Arrays.asList(
                new StudentDto("John", "Doe", "john.doe@example.com", 20),
                new StudentDto("Jane", "Doe", "jane.doe@example.com", 22)
        );
        //when
        when(service.getAllStudents()).thenReturn(students);

        //then
        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].email", is("jane.doe@example.com")));
    }

    @Test
    void shouldReturnStudentById() throws Exception {
        //given & when
        when(service.getStudentById(1L)).thenReturn(studentDto);

        //then
        mockMvc.perform(get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void shouldReturn404WhenStudentNotFound() throws Exception {
        //given & when
        when(service.getStudentById(1L))
                .thenThrow(new StudentNotFoundException("Student with ID 1 not found"));

        //then
        mockMvc.perform(get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student with ID 1 not found"));
    }

    @Test
    void shouldCreateStudent() throws Exception {
        //given & when
        when(service.createStudent(any(StudentDto.class))).thenReturn(studentDto);

        //then
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"age\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void shouldReturn400WhenDuplicateEmail() throws Exception {
        //given & when
        when(service.createStudent(any(StudentDto.class)))
                .thenThrow(new DuplicateStudentException("A student with email john.doe@example.com already exists"));

        //then
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"age\":20}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A student with email john.doe@example.com already exists"));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        //given & when
        when(service.updateStudent(anyLong(), any(StudentDto.class))).thenReturn(studentDto);

        //then
        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"age\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        //given & when
        Mockito.doNothing().when(service).deleteStudent(1L);

        //then
        mockMvc.perform(delete("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentStudent() throws Exception {
        //given & when
        Mockito.doThrow(new StudentNotFoundException("Student with ID 1 not found"))
                .when(service).deleteStudent(1L);

        //then
        mockMvc.perform(delete("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student with ID 1 not found"));
    }
}
