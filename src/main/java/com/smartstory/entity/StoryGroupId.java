package com.smartstory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryGroupId implements java.io.Serializable {

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "group_id")
    private Long groupId;
}