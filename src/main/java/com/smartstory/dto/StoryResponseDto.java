package com.smartstory.dto;

import com.smartstory.entity.Mode;

import java.util.List;

public class StoryResponseDto {
    private Long id;
    private String content;
    private Mode mode;
    private List<GroupDto> storyGroups;

    public StoryResponseDto() {
    }

    public StoryResponseDto(Long id, String content, Mode mode, List<GroupDto> storyGroups) {
        this.id = id;
        this.content = content;
        this.mode = mode;
        this.storyGroups = storyGroups;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<GroupDto> getStoryGroups() {
        return storyGroups;
    }

    public void setStoryGroups(List<GroupDto> storyGroups) {
        this.storyGroups = storyGroups;
    }
}
