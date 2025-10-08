package com.epam.java.persistance.demo.mapper;

import com.epam.java.persistance.demo.domain.GroupDto;
import com.epam.java.persistance.demo.entity.Group;
import com.epam.java.persistance.demo.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "studentIds", source = "students", qualifiedByName = "studentsToIds")
    @Mapping(target = "currentEnrollment", expression = "java(group.getStudents().size())")
    GroupDto toGroupDto(Group group);

    @Mapping(target = "students", ignore = true)
    Group toGroupEntity(GroupDto dto);

    @Named("studentsToIds")
    default Set<Long> studentsToIds(Set<Student> students) {
        if (students == null) {
            return Set.of();
        }
        return students.stream()
                .map(Student::getId)
                .collect(Collectors.toSet());
    }
}
