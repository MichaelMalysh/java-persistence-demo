package com.epam.java.persistance.demo.controller;

import com.epam.java.persistance.demo.domain.GroupDto;
import com.epam.java.persistance.demo.domain.GroupFilterDto;
import com.epam.java.persistance.demo.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDto>> getGroups(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String studentEmail,
            @RequestParam(required = false) Boolean orderedByCount) {
        GroupFilterDto filter = new GroupFilterDto(active, available, studentEmail, orderedByCount);
        return ResponseEntity.ok(groupService.getGroupsFiltered(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<GroupDto> getGroupByCode(@PathVariable String code) {
        return ResponseEntity.ok(groupService.getGroupByCode(code));
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.createGroup(groupDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable Long id,
            @RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.updateGroup(id, groupDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<GroupDto> addStudentToGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(groupService.addStudentToGroup(groupId, studentId));
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<GroupDto> removeStudentFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(groupService.removeStudentFromGroup(groupId, studentId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GroupDto>> searchGroups(@RequestParam String keyword) {
        return ResponseEntity.ok(groupService.searchGroups(keyword));
    }
}
