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

    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
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

    public List<GroupDto> getActiveGroups() {
        return groupRepository.findActiveGroups(LocalDate.now()).stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
    }

    public List<GroupDto> getGroupsWithAvailableSpots() {
        return groupRepository.findGroupsWithAvailableSpots().stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
    }

    public List<GroupDto> searchGroups(String keyword) {
        return groupRepository.searchByNameOrCode(keyword).stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
    }

    public List<GroupDto> getGroupsByStudentEmail(String email) {
        return groupRepository.findGroupsByStudentEmail(email).stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());
    }

    public List<GroupDto> getGroupsOrderedByStudentCount() {
        List<Long> orderedIds = groupRepository.findGroupIdsOrderedByStudentCount();
        List<Group> groups = groupRepository.findByIdWithStudents(orderedIds);

        Map<Long, Group> groupMap = groups.stream()
                .collect(Collectors.toMap(Group::getId, g -> g));

        return orderedIds.stream()
                .map(groupMap::get)
                .map(groupMapper::toGroupDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
