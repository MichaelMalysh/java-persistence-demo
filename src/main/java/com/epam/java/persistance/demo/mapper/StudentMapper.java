package com.epam.java.persistance.demo.mapper;

import com.epam.java.persistance.demo.domain.StudentDto;
import com.epam.java.persistance.demo.entity.Student;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDto toStudentDto(Student student);

    Student toStudentEntity(StudentDto studentDto);
}
