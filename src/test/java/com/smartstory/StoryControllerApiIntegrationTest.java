package com.smartstory;

import com.smartstory.entity.Group;
import com.smartstory.entity.GroupMember;
import com.smartstory.entity.User;
import com.smartstory.repository.GroupMemberRepository;
import com.smartstory.repository.GroupRepository;
import com.smartstory.repository.StoryExceptionRepository;
import com.smartstory.repository.StoryGroupRepository;
import com.smartstory.repository.StoryRepository;
import com.smartstory.repository.StoryRuleRepository;
import com.smartstory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StoryControllerApiIntegrationTest {

    private static final Pattern STORY_ID_PATTERN = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)");

    @Autowired
    private MockMvc mockMvc;

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

        alice = userRepository.save(createUser("Alice", "alice_api@example.com"));
        bob = userRepository.save(createUser("Bob", "bob_api@example.com"));
        charlie = userRepository.save(createUser("Charlie", "charlie_api@example.com"));

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
    void createStory_returnsStoryGroupsInResponse() throws Exception {
        String requestBody = """
                {
                  "userId": %d,
                  "content": "API Story",
                  "mode": "SHOW",
                  "groupIds": [%d],
                  "exceptionUserIds": []
                }
                """.formatted(alice.getId(), friends.getId());

        mockMvc.perform(post("/api/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value("API Story"))
                .andExpect(jsonPath("$.mode").value("SHOW"))
                .andExpect(jsonPath("$.storyGroups").isArray())
                .andExpect(jsonPath("$.storyGroups[0].id").value(friends.getId()))
                .andExpect(jsonPath("$.storyGroups[0].name").value("Friends"));
    }

    @Test
    void visibilityEndpoint_showMode_memberCanView_nonMemberCannotView() throws Exception {
        Long storyId = createStoryAndGetId(alice.getId(), "SHOW Story", "SHOW", new Long[]{friends.getId()}, new Long[]{});

        mockMvc.perform(get("/api/stories/{storyId}/visibility", storyId)
                        .param("userId", String.valueOf(bob.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canView").value(true));

        mockMvc.perform(get("/api/stories/{storyId}/visibility", storyId)
                        .param("userId", String.valueOf(charlie.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canView").value(false));
    }

    @Test
    void visibilityEndpoint_hideMode_exceptionUserBlocked() throws Exception {
        Long storyId = createStoryAndGetId(alice.getId(), "HIDE Story", "HIDE", new Long[]{friends.getId()}, new Long[]{bob.getId()});

        mockMvc.perform(get("/api/stories/{storyId}/visibility", storyId)
                        .param("userId", String.valueOf(bob.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canView").value(false));
    }

    private Long createStoryAndGetId(Long userId, String content, String mode, Long[] groupIds, Long[] exceptionUserIds) throws Exception {
        String payload = toStoryPayloadJson(userId, content, mode, groupIds, exceptionUserIds);

        String response = mockMvc.perform(post("/api/stories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Matcher matcher = STORY_ID_PATTERN.matcher(response);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not parse story id from response: " + response);
        }
        return Long.parseLong(matcher.group(1));
    }

    private String toStoryPayloadJson(Long userId, String content, String mode, Long[] groupIds, Long[] exceptionUserIds) {
        return "{"
                + "\"userId\":" + userId + ","
                + "\"content\":\"" + content + "\","
                + "\"mode\":\"" + mode + "\","
                + "\"groupIds\":" + toJsonArray(groupIds) + ","
                + "\"exceptionUserIds\":" + toJsonArray(exceptionUserIds)
                + "}";
    }

    private String toJsonArray(Long[] values) {
        if (values == null || values.length == 0) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[");
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(values[index]);
        }
        builder.append(']');
        return builder.toString();
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        return user;
    }

}
