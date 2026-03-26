package com.smartstory.dto;

import com.smartstory.entity.Mode;
import java.util.List;

public class StoryCreateRequest {
    private Long userId;
    private String content;
    private Mode mode;
    private List<Long> groupIds;
    private List<Long> exceptionUserIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Long> getExceptionUserIds() {
        return exceptionUserIds;
    }

    public void setExceptionUserIds(List<Long> exceptionUserIds) {
        this.exceptionUserIds = exceptionUserIds;
    }
}
