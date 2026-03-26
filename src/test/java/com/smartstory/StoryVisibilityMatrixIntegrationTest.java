package com.smartstory;

import com.smartstory.entity.Group;
import com.smartstory.entity.GroupMember;
import com.smartstory.entity.Mode;
import com.smartstory.entity.Story;
import com.smartstory.entity.User;
import com.smartstory.repository.GroupMemberRepository;
import com.smartstory.repository.GroupRepository;
import com.smartstory.repository.StoryExceptionRepository;
import com.smartstory.repository.StoryGroupRepository;
import com.smartstory.repository.StoryRepository;
import com.smartstory.repository.StoryRuleRepository;
import com.smartstory.repository.UserRepository;
import com.smartstory.service.StoryCreationRequest;
import com.smartstory.service.StoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StoryVisibilityMatrixIntegrationTest {

    @Autowired
    private StoryService storyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private StoryExceptionRepository storyExceptionRepository;

    @Autowired
    private StoryGroupRepository storyGroupRepository;

    @Autowired
    private StoryRuleRepository storyRuleRepository;

    @Autowired
    private StoryRepository storyRepository;

    private User alice;
    private User bob;
    private User charlie;
    private Group friends;

    @BeforeEach
    void setUp() {
        storyExceptionRepository.deleteAll();
        storyGroupRepository.deleteAll();
        storyRuleRepository.deleteAll();
        groupMemberRepository.deleteAll();
        storyRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();

        alice = userRepository.save(createUser("Alice", "alice_test@example.com"));
        bob = userRepository.save(createUser("Bob", "bob_test@example.com"));
        charlie = userRepository.save(createUser("Charlie", "charlie_test@example.com"));

        friends = new Group();
        friends.setName("Friends");
        friends.setUser(alice);
        friends = groupRepository.save(friends);

        GroupMember aliceMember = new GroupMember();
        aliceMember.setGroup(friends);
        aliceMember.setUser(alice);
        groupMemberRepository.save(aliceMember);

        GroupMember bobMember = new GroupMember();
        bobMember.setGroup(friends);
        bobMember.setUser(bob);
        groupMemberRepository.save(bobMember);
    }

    @Test
    void case1_showFriends_creatorCanView() {
        Story story = createStory("Hello Friends", Mode.SHOW, true, false);
        assertCanView(story.getId(), alice.getId(), true);
    }

    @Test
    void case2_showFriends_bobCanView() {
        Story story = createStory("Hello Friends", Mode.SHOW, true, false);
        assertCanView(story.getId(), bob.getId(), true);
    }

    @Test
    void case3_showFriends_charlieCannotView() {
        Story story = createStory("Hello Friends", Mode.SHOW, true, false);
        assertCanView(story.getId(), charlie.getId(), false);
    }

    @Test
    void case4_hideFriends_creatorCanView() {
        Story story = createStory("Secret Message", Mode.HIDE, true, false);
        assertCanView(story.getId(), alice.getId(), true);
    }

    @Test
    void case5_hideFriends_bobCannotView() {
        Story story = createStory("Secret Message", Mode.HIDE, true, false);
        assertCanView(story.getId(), bob.getId(), false);
    }

    @Test
    void case6_hideFriends_charlieCanView() {
        Story story = createStory("Secret Message", Mode.HIDE, true, false);
        assertCanView(story.getId(), charlie.getId(), true);
    }

    @Test
    void case7_showFriends_charlieExceptionCannotView() {
        Story story = createStory("VIP Only", Mode.SHOW, true, true);
        assertCanView(story.getId(), charlie.getId(), false);
    }

    @Test
    void case8_showFriends_bobNotExceptionCanView() {
        Story story = createStory("VIP Only", Mode.SHOW, true, true);
        assertCanView(story.getId(), bob.getId(), true);
    }

    @Test
    void case9_hideFriends_bobExceptionCannotView() {
        Story story = createStory("Private Hide", Mode.HIDE, true, false, bob.getId());
        assertCanView(story.getId(), bob.getId(), false);
    }

    @Test
    void case10_hideFriends_charlieCanView() {
        Story story = createStory("Private Hide", Mode.HIDE, true, false, bob.getId());
        assertCanView(story.getId(), charlie.getId(), true);
    }

    @Test
    void case11_showNoGroups_publicCanView() {
        Story story = createStory("Public Show", Mode.SHOW, false, false);
        assertCanView(story.getId(), bob.getId(), true);
    }

    private Story createStory(String content, Mode mode, boolean includeFriendsGroup, boolean includeCharlieException, Long... explicitExceptionIds) {
        StoryCreationRequest request = new StoryCreationRequest();
        request.setUserId(alice.getId());
        request.setContent(content);
        request.setMode(mode);

        if (includeFriendsGroup) {
            request.setGroupIds(java.util.List.of(friends.getId()));
        } else {
            request.setGroupIds(java.util.List.of());
        }

        java.util.List<Long> exceptionUserIds = new java.util.ArrayList<>();
        if (includeCharlieException) {
            exceptionUserIds.add(charlie.getId());
        }
        if (explicitExceptionIds != null && explicitExceptionIds.length > 0) {
            exceptionUserIds.addAll(java.util.List.of(explicitExceptionIds));
        }
        request.setExceptionUserIds(exceptionUserIds);

        return storyService.createStoryWithRules(request);
    }

    private void assertCanView(Long storyId, Long viewerId, boolean expected) {
        boolean actual = storyService.canUserViewStory(storyId, viewerId);
        assertEquals(expected, actual, "Unexpected visibility for storyId=" + storyId + ", viewerId=" + viewerId);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        return user;
    }
}
