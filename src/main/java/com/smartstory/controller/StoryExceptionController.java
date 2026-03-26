package com.smartstory.controller;

import com.smartstory.dto.StoryExceptionRequest;
import com.smartstory.service.StoryExceptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/story-exceptions")
public class StoryExceptionController {

    private final StoryExceptionService storyExceptionService;

    public StoryExceptionController(StoryExceptionService storyExceptionService) {
        this.storyExceptionService = storyExceptionService;
    }

    @PostMapping
    public ResponseEntity<Void> addExceptions(@RequestBody StoryExceptionRequest request) {
        storyExceptionService.addExceptions(request);
        return ResponseEntity.ok().build();
    }
}