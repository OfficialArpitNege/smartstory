package com.smartstory.dto;

import java.util.List;

public class StoryExceptionRequest {
    private Long storyId;
    private List<Long> userIds;

    public StoryExceptionRequest() {
    }

    public StoryExceptionRequest(Long storyId, List<Long> userIds) {
        this.storyId = storyId;
        this.userIds = userIds;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}