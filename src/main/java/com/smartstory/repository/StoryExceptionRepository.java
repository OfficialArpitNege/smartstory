package com.smartstory.repository;

import com.smartstory.entity.StoryException;
import com.smartstory.entity.Story;
import com.smartstory.entity.User;
import com.smartstory.entity.StoryExceptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryExceptionRepository extends JpaRepository<StoryException, StoryExceptionId> {
    List<StoryException> findByStory(Story story);
    List<StoryException> findByUser(User user);
}
