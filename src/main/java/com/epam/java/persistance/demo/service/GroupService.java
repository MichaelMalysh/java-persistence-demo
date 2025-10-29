package com.epam.java.persistance.demo.service;

import com.epam.java.persistance.demo.domain.GroupDto;

import java.util.List;

public interface GroupService {

    List<GroupDto> getGroupsFiltered(Boolean active, Boolean available, String studentEmail, Boolean orderedByCount);
    GroupDto getGroupById(Long id);
    GroupDto getGroupByCode(String code);
    GroupDto createGroup(GroupDto groupDto);
    GroupDto updateGroup(Long id, GroupDto groupDto);
    void deleteGroup(Long id);
    GroupDto addStudentToGroup(Long groupId, Long studentId);
    GroupDto removeStudentFromGroup(Long groupId, Long studentId);
    List<GroupDto> searchGroups(String keyword);
}
