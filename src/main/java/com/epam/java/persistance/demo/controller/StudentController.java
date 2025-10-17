package com.epam.java.persistance.demo.controller;


import com.epam.java.persistance.demo.domain.StudentDto;
import com.epam.java.persistance.demo.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @GetMapping
    public String getAllStudents(Model model) {
        model.addAttribute("students", service.getAllStudents()); // List of StudentDto
        return "students"; // students.html
    }

    @GetMapping("/specific/{id}")
    public String getStudent(@PathVariable Long id, Model model) {
        model.getAttribute("students");
        return "redirect:/specific-student"; // specific-student.html
    }

    @GetMapping("/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new StudentDto(null, "", "", "", 0));
        return "student-form"; // student-form.html
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute("student") StudentDto studentDto, BindingResult result) {
        if (result.hasErrors()) {
            return "student-form"; // Return form with errors
        }
        service.createStudent(studentDto);
        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", service.getStudentById(id)); // Fetch StudentDto by ID
        return "student-form"; // Reuse student-form.html for editing
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") StudentDto studentDto) {
        service.updateStudent(id, studentDto); // Update student via service
        return "redirect:/students"; // Redirect to student list
    }

    @GetMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id); // Delete student via service
        return "redirect:/students"; // Redirect to student list
    }
}