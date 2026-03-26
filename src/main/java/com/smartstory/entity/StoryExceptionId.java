package com.smartstory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryExceptionId implements java.io.Serializable {

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "user_id")
    private Long userId;
}