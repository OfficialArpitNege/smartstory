package com.smartstory.repository;

import com.smartstory.entity.Story;
import com.smartstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUser(User user);
}
