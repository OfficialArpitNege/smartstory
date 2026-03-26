package com.smartstory.controller;

import com.smartstory.dto.StoryCreateRequest;
import com.smartstory.dto.StoryResponseDto;
import com.smartstory.dto.StoryVisibilityResponse;
import com.smartstory.entity.Story;
import com.smartstory.service.StoryCreationRequest;
import com.smartstory.service.StoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping
    public ResponseEntity<StoryResponseDto> createStory(@RequestBody StoryCreateRequest request) {
        StoryCreationRequest creationRequest = new StoryCreationRequest();
        creationRequest.setUserId(request.getUserId());
        creationRequest.setContent(request.getContent());
        creationRequest.setMode(request.getMode());
        creationRequest.setGroupIds(request.getGroupIds());
        creationRequest.setExceptionUserIds(request.getExceptionUserIds());

        Story story = storyService.createStoryWithRules(creationRequest);
        StoryResponseDto response = storyService.mapToStoryResponseDto(story);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<StoryResponseDto> getStoryById(@PathVariable Long storyId) {
        StoryResponseDto response = storyService.getStoryResponseById(storyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storyId}/visibility")
    public ResponseEntity<StoryVisibilityResponse> checkVisibility(@PathVariable Long storyId,
                                                                   @RequestParam Long userId) {
        boolean canView = storyService.canUserViewStory(storyId, userId);
        StoryVisibilityResponse response = new StoryVisibilityResponse(storyId, userId, canView);
        return ResponseEntity.ok(response);
    }
}
