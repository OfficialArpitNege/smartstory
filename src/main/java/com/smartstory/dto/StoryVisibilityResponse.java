package com.smartstory.dto;

public class StoryVisibilityResponse {
    private Long storyId;
    private Long userId;
    private boolean canView;

    public StoryVisibilityResponse() {
    }

    public StoryVisibilityResponse(Long storyId, Long userId, boolean canView) {
        this.storyId = storyId;
        this.userId = userId;
        this.canView = canView;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }
}
