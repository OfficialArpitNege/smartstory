package com.smartstory.service;

import com.smartstory.dto.StoryExceptionRequest;
import com.smartstory.entity.Story;
import com.smartstory.entity.StoryException;
import com.smartstory.entity.StoryExceptionId;
import com.smartstory.entity.User;
import com.smartstory.repository.StoryExceptionRepository;
import com.smartstory.repository.StoryRepository;
import com.smartstory.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryExceptionService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryExceptionRepository storyExceptionRepository;

    public StoryExceptionService(StoryRepository storyRepository,
                                 UserRepository userRepository,
                                 StoryExceptionRepository storyExceptionRepository) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.storyExceptionRepository = storyExceptionRepository;
    }

    @Transactional
    public void addExceptions(StoryExceptionRequest request) {
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("Story not found: " + request.getStoryId()));

        for (Long userId : request.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            StoryException storyException = new StoryException();
            storyException.setId(new StoryExceptionId(story.getId(), user.getId()));
            storyException.setStory(story);
            storyException.setUser(user);

            storyExceptionRepository.save(storyException);
        }
    }
}