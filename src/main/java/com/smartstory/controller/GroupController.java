package com.smartstory.controller;

import com.smartstory.dto.GroupCreateRequest;
import com.smartstory.dto.GroupMemberRequest;
import com.smartstory.entity.Group;
import com.smartstory.entity.GroupMember;
import com.smartstory.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
public ResponseEntity<String> createGroup(@RequestBody GroupCreateRequest request) {
    Group group = groupService.createGroup(request.getOwnerId(), request.getGroupName());
    return ResponseEntity.ok("Group created with id: " + group.getId());
}

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupMember> addMember(@PathVariable Long groupId, @RequestBody GroupMemberRequest request) {
        GroupMember member = groupService.addMember(groupId, request.getUserId());
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeMember(groupId, userId);
        return ResponseEntity.noContent().build();
    }
}
