package com.smartstory;

import com.smartstory.entity.Mode;
import com.smartstory.entity.User;
import com.smartstory.repository.UserRepository;
import com.smartstory.service.GroupService;
import com.smartstory.service.StoryCreationRequest;
import com.smartstory.service.StoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@SpringBootApplication
public class SmartStoryApplication {

	private static final Logger log = LoggerFactory.getLogger(SmartStoryApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SmartStoryApplication.class, args);
	}
	// @Bean
	// public CommandLineRunner demoRunner(UserRepository userRepository,
	//                                    GroupService groupService,
	//                                    StoryService storyService) {
	// 	return args -> {
	// 		log.info("=== SmartStory Demo Runner Started ===");

	// 		User alice = new User();
	// 		alice.setName("Alice");
	// 		alice.setEmail("alice@example.com");
	// 		alice.setPassword("password");
	// 		alice = userRepository.save(alice);

	// 		User bob = new User();
	// 		bob.setName("Bob");
	// 		bob.setEmail("bob@example.com");
	// 		bob.setPassword("password");
	// 		bob = userRepository.save(bob);

	// 		User charlie = new User();
	// 		charlie.setName("Charlie");
	// 		charlie.setEmail("charlie@example.com");
	// 		charlie.setPassword("password");
	// 		charlie = userRepository.save(charlie);

	// 		var friendsGroup = groupService.createGroup(alice.getId(), "Friends");
	// 		log.info("Created group {} (id={}) by user {}", friendsGroup.getName(), friendsGroup.getId(), alice.getName());

	// 		groupService.addMember(friendsGroup.getId(), bob.getId());
	// 		log.info("Added {} to group {}", bob.getName(), friendsGroup.getName());

	// 		StoryCreationRequest storyRequest = new StoryCreationRequest();
	// 		storyRequest.setUserId(alice.getId());
	// 		storyRequest.setContent("Hello friends, this is a secret story!");
	// 		storyRequest.setMode(Mode.SHOW);
	// 		storyRequest.setGroupIds(java.util.List.of(friendsGroup.getId()));
	// 		storyRequest.setExceptionUserIds(java.util.List.of(charlie.getId()));

	// 		var story = storyService.createStoryWithRules(storyRequest);
	// 		log.info("Created story id={} by {} with mode={} and group={} and exception={} ", story.getId(), alice.getName(), story.getStoryRule().getMode(), friendsGroup.getName(), charlie.getName());

	// 		boolean bobCanSee = storyService.canUserViewStory(story.getId(), bob.getId());
	// 		boolean charlieCanSee = storyService.canUserViewStory(story.getId(), charlie.getId());
	// 		boolean aliceCanSee = storyService.canUserViewStory(story.getId(), alice.getId());

	// 		log.info("Visibility: bob={} (expect true), charlie={} (expect true exception), alice={} (expect true owner)", bobCanSee, charlieCanSee, aliceCanSee);

	// 		log.info("=== SmartStory Demo Runner Finished ===");
	// 	};
	// }
}
