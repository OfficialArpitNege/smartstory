package com.smartstory.service;

import com.smartstory.dto.GroupDto;
import com.smartstory.dto.StoryResponseDto;
import com.smartstory.entity.*;
import com.smartstory.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final StoryRuleRepository storyRuleRepository;
    private final StoryGroupRepository storyGroupRepository;
    private final StoryExceptionRepository storyExceptionRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public StoryService(UserRepository userRepository,
                        StoryRepository storyRepository,
                        StoryRuleRepository storyRuleRepository,
                        StoryGroupRepository storyGroupRepository,
                        StoryExceptionRepository storyExceptionRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.storyRuleRepository = storyRuleRepository;
        this.storyGroupRepository = storyGroupRepository;
        this.storyExceptionRepository = storyExceptionRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional
    public Story createStoryWithRules(StoryCreationRequest request) {
        User author = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Story content cannot be blank");
        }

        if (request.getMode() == null) {
            throw new IllegalArgumentException("Story mode must be provided");
        }

        Story story = new Story();
        story.setUser(author);
        story.setContent(request.getContent());
        story = storyRepository.save(story);

        StoryRule rule = new StoryRule();
        rule.setStory(story);
        rule.setMode(request.getMode());
        storyRuleRepository.save(rule);

        storeStoryGroups(request, story);
        storeStoryExceptions(request, story);

        story.setStoryRule(rule);
        return story;
    }

    @Transactional(readOnly = true)
    public StoryResponseDto getStoryResponseById(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found: " + storyId));
        return mapToStoryResponseDto(story);
    }

    public StoryResponseDto mapToStoryResponseDto(Story story) {
        List<StoryGroup> storyGroups = storyGroupRepository.findByStory(story);

        List<GroupDto> groupDtos = storyGroups.stream()
                .map(storyGroup -> new GroupDto(storyGroup.getGroup().getId(), storyGroup.getGroup().getName()))
                .collect(Collectors.toList());

        Mode mode = null;
        if (story.getStoryRule() != null) {
            mode = story.getStoryRule().getMode();
        }

        return new StoryResponseDto(
                story.getId(),
                story.getContent(),
                mode,
                groupDtos == null ? Collections.emptyList() : groupDtos
        );
    }

private void storeStoryGroups(StoryCreationRequest request, Story story) {

    if (request.getGroupIds() == null || request.getGroupIds().isEmpty()) {
        return;
    }

    for (Long groupId : request.getGroupIds()) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        StoryGroup sg = new StoryGroup();

        // 🔥 ID set karna MUST hai
        StoryGroupId id = new StoryGroupId(
                story.getId(),
                group.getId()
        );

        sg.setId(id);

        sg.setStory(story);
        sg.setGroup(group);

        storyGroupRepository.save(sg);
    }
}

    private void storeStoryExceptions(StoryCreationRequest request, Story story) {
        if (request.getExceptionUserIds() == null || request.getExceptionUserIds().isEmpty()) {
            return;
        }

        for (Long userId : request.getExceptionUserIds()) {
            User exceptionUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Exception user not found: " + userId));

            StoryException storyException = new StoryException();
            storyException.setId(new StoryExceptionId(story.getId(), exceptionUser.getId()));
            storyException.setStory(story);
            storyException.setUser(exceptionUser);
            storyExceptionRepository.save(storyException);
        }
    }

    @Transactional(readOnly = true)
    public boolean canUserViewStory(Long storyId, Long viewerId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found: " + storyId));

        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + viewerId));

        if (story.getUser() != null && story.getUser().getId().equals(viewerId)) {
            return true;
        }

        List<StoryException> exceptions = storyExceptionRepository.findByStory(story);
        if (exceptions.stream().anyMatch(it -> it.getUser().getId().equals(viewerId))) {
            return false;
        }

        StoryRule storyRule = story.getStoryRule();
        if (storyRule == null) {
            return false;
        }

        Mode mode = storyRule.getMode();
        List<StoryGroup> storyGroups = storyGroupRepository.findByStory(story);

        Set<Long> groupIds = storyGroups.stream()
                .map(it -> it.getGroup().getId())
                .collect(Collectors.toSet());

        List<GroupMember> viewerMemberships = groupMemberRepository.findByUser(viewer);
        Set<Long> viewerGroupIds = viewerMemberships.stream()
                .map(it -> it.getGroup().getId())
                .collect(Collectors.toSet());

        Set<Long> intersect = new HashSet<>(groupIds);
        intersect.retainAll(viewerGroupIds);

        if (mode == Mode.SHOW) {
            if (groupIds.isEmpty()) {
                return true;
            }
            return !intersect.isEmpty();
        }

        if (mode == Mode.HIDE) {
            return intersect.isEmpty();
        }

        return false;
    }
}
