package com.smartstory.repository;

import com.smartstory.entity.StoryGroup;
import com.smartstory.entity.Story;
import com.smartstory.entity.Group;
import com.smartstory.entity.StoryGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryGroupRepository extends JpaRepository<StoryGroup, StoryGroupId> {
    List<StoryGroup> findByStory(Story story);
    List<StoryGroup> findByGroup(Group group);
}
