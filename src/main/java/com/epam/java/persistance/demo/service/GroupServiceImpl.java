package com.epam.java.persistance.demo.service;

import com.epam.java.persistance.demo.domain.GroupDto;
import com.epam.java.persistance.demo.entity.Group;
import com.epam.java.persistance.demo.entity.Student;
import com.epam.java.persistance.demo.mapper.GroupMapper;
import com.epam.java.persistance.demo.repository.GroupRepository;
import com.epam.java.persistance.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;


    public List<GroupDto> getGroupsFiltered(Boolean active, Boolean available, String studentEmail, Boolean orderedByCount) {
        List<Group> groups = groupRepository.findAll();

        groups = filter(active, available, studentEmail, groups);

        List<GroupDto> orderedIds = applyOrdering(orderedByCount, groups);
        if (orderedIds != null) return orderedIds;

        return groups.stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
    }

    private List<Group> filter(Boolean active, Boolean available, String studentEmail, List<Group> groups) {
        groups = applyStudentEmailFilter(studentEmail, groups);

        groups = applyAvailableFilter(available, groups);

        groups = applyActiveFilter(active, groups);
        return groups;
    }

    private List<GroupDto> applyOrdering(Boolean orderedByCount, List<Group> groups) {
        if (Boolean.TRUE.equals(orderedByCount)) {
            List<Long> orderedIds = groupRepository.findGroupIdsOrderedByStudentCount();
            Map<Long, Group> groupMap = groups.stream()
                    .collect(Collectors.toMap(Group::getId, g -> g));

            return orderedIds.stream()
                    .map(groupMap::get)
                    .filter(Objects::nonNull)
                    .map(groupMapper::toGroupDto)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private static List<Group> applyActiveFilter(Boolean active, List<Group> groups) {
        if (Boolean.TRUE.equals(active)) {
            LocalDate now = LocalDate.now();
            groups = groups.stream()
                    .filter(g -> !g.getStartDate().isAfter(now) && !g.getEndDate().isBefore(now))
                    .collect(Collectors.toList());
        }
        return groups;
    }

    private static List<Group> applyAvailableFilter(Boolean available, List<Group> groups) {
        if (Boolean.TRUE.equals(available)) {
            groups = groups.stream()
                    .filter(g -> g.getStudents().size() < g.getMaxCapacity())
                    .collect(Collectors.toList());
        }
        return groups;
    }

    private List<Group> applyStudentEmailFilter(String studentEmail, List<Group> groups) {
        if (studentEmail != null && !studentEmail.isEmpty()) {
            List<Long> studentGroupIds = groupRepository.findGroupsByStudentEmail(studentEmail)
                    .stream()
                    .map(Group::getId)
                    .toList();
            groups = groups.stream()
                    .filter(g -> studentGroupIds.contains(g.getId()))
                    .collect(Collectors.toList());
        }
        return groups;
    }

    public GroupDto getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));
        return groupMapper.toGroupDto(group);
    }

    public GroupDto getGroupByCode(String code) {
        Group group = groupRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Group not found with code: " + code));
        return groupMapper.toGroupDto(group);
    }

    @Transactional
    public GroupDto createGroup(GroupDto groupDto) {
        if (groupRepository.existsByCode(groupDto.code())) {
            throw new RuntimeException("Group with code " + groupDto.code() + " already exists");
        }

        if (groupDto.startDate().isAfter(groupDto.endDate())) {
            throw new RuntimeException("Start date must be before end date");
        }

        Group group = groupMapper.toGroupEntity(groupDto);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toGroupDto(savedGroup);
    }

    @Transactional
    public GroupDto updateGroup(Long id, GroupDto groupDto) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));

        if (!existingGroup.getCode().equals(groupDto.code()) &&
                groupRepository.existsByCode(groupDto.code())) {
            throw new RuntimeException("Group with code " + groupDto.code() + " already exists");
        }

        existingGroup.setName(groupDto.name());
        existingGroup.setCode(groupDto.code());
        existingGroup.setStartDate(groupDto.startDate());
        existingGroup.setEndDate(groupDto.endDate());
        existingGroup.setMaxCapacity(groupDto.maxCapacity());
        existingGroup.setDescription(groupDto.description());

        Group updatedGroup = groupRepository.save(existingGroup);
        return groupMapper.toGroupDto(updatedGroup);
    }

    @Transactional
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
    }

    @Transactional
    public GroupDto addStudentToGroup(Long groupId, Long studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        if (group.getStudents().size() >= group.getMaxCapacity()) {
            throw new RuntimeException("Group has reached maximum capacity");
        }

        if (group.getStudents().contains(student)) {
            throw new RuntimeException("Student is already enrolled in this group");
        }

        group.getStudents().add(student);
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toGroupDto(updatedGroup);
    }

    @Transactional
    public GroupDto removeStudentFromGroup(Long groupId, Long studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        if (!group.getStudents().contains(student)) {
            throw new RuntimeException("Student is not enrolled in this group");
        }

        group.getStudents().remove(student);
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toGroupDto(updatedGroup);
    }

    @Transactional
    public List<GroupDto> searchGroups(String keyword) {
        return groupRepository.searchByNameOrCode(keyword).stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
    }
}
