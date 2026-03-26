package com.smartstory.service;

import com.smartstory.entity.Group;
import com.smartstory.entity.GroupMember;
import com.smartstory.entity.User;
import com.smartstory.repository.GroupMemberRepository;
import com.smartstory.repository.GroupRepository;
import com.smartstory.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository,
                        GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional
    public Group createGroup(Long ownerId, String groupName) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found: " + ownerId));

        Group group = new Group();
        group.setName(groupName);
        group.setUser(owner);
        Group saved = groupRepository.save(group);

        GroupMember ownerMembership = new GroupMember();
        ownerMembership.setGroup(saved);
        ownerMembership.setUser(owner);
        groupMemberRepository.save(ownerMembership);

        return saved;
    }

    @Transactional
    public GroupMember addMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Optional<GroupMember> existing = groupMemberRepository.findByGroupAndUser(group, user);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already in group");
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);

        return groupMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new IllegalStateException("User is not a member of group"));

        groupMemberRepository.delete(member);
    }
}
