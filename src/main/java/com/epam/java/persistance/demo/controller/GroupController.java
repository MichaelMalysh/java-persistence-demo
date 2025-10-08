package com.epam.java.persistance.demo.controller;

import com.epam.java.persistance.demo.domain.GroupDto;
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
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/code/{code}")
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

    @GetMapping("/active")
    public ResponseEntity<List<GroupDto>> getActiveGroups() {
        return ResponseEntity.ok(groupService.getActiveGroups());
    }

    @GetMapping("/available")
    public ResponseEntity<List<GroupDto>> getGroupsWithAvailableSpots() {
        return ResponseEntity.ok(groupService.getGroupsWithAvailableSpots());
    }

    @GetMapping("/search")
    public ResponseEntity<List<GroupDto>> searchGroups(@RequestParam String keyword) {
        return ResponseEntity.ok(groupService.searchGroups(keyword));
    }

    @GetMapping("/student/{email}")
    public ResponseEntity<List<GroupDto>> getGroupsByStudentEmail(@PathVariable String email) {
        return ResponseEntity.ok(groupService.getGroupsByStudentEmail(email));
    }

    @GetMapping("/ordered-by-count")
    public ResponseEntity<List<GroupDto>> getGroupsOrderedByStudentCount() {
        return ResponseEntity.ok(groupService.getGroupsOrderedByStudentCount());
    }
}
