package com.example.monzun;

import com.example.monzun.entities.*;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.enums.TaskStatusEnum;
import com.example.monzun.repositories.*;
import com.example.monzun.security.JwtRequestFilter;
import com.example.monzun.security.JwtUtil;
import com.example.monzun.services.MyUserDetailsService;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskCommentControllerTest extends BaseTest {
    private final String TEST_STARTUP_EMAIL = "TESTSTARTUP@ya.ru";
    private final String TEST_TRACKER_EMAIL = "TESTTRACKER@mail.ru";
    private Startup startup;
    private User tracker;
    private Tracking tracking;
    private String jwt;
    private final String API_PREFIX = "/api/task-comments/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private StartupTrackingRepository startupTrackingRepository;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    public void setup() {
        createStartupOwner();
        tracker = createTracker();
        startup = createTestStartupObject();
        tracking = createTrackingWithStartup(startup);
    }

    @Test
    @Order(1)
    public void getTaskCommentsByStartupTest() throws Exception {
        authUser(TEST_STARTUP_EMAIL);

        Task task = createTask(tracking, startup, tracker);
        TaskComment taskComment = createTaskComment(task, tracker);

        mockMvc.perform(
                get(API_PREFIX + task.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(taskComment.getId().intValue())))
                .andExpect(jsonPath("$[0].text", is(taskComment.getText())));
    }

    @Test
    @Order(2)
    public void getTaskCommentsByTrackerTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);

        Task task = createTask(tracking, startup, tracker);
        TaskComment taskComment = createTaskComment(task, tracker);

        mockMvc.perform(
                get(API_PREFIX + task.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(taskComment.getId().intValue())))
                .andExpect(jsonPath("$[0].text", is(taskComment.getText())));
    }


    @Test
    @Order(3)
    public void createTaskCommentTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);

        Task task = createTask(tracking, startup, tracker);
        String text = faker.bothify("????????????????#########??????????????#########");

        JSONObject params = new JSONObject();
        params.put("text", text);

        mockMvc.perform(
                post(API_PREFIX + task.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params.toString())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(text)));
    }


    @Test
    @Order(4)
    public void updateTaskTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);

        Task task = createTask(tracking, startup, tracker);
        String text = faker.bothify("????????????????#########??????????????#########");
        TaskComment taskComment = createTaskComment(task, tracker);

        JSONObject params = new JSONObject();
        params.put("text", text);

        mockMvc.perform(
                put(API_PREFIX + taskComment.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params.toString())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskComment.getId().intValue())))
                .andExpect(jsonPath("$.text", is(text)));
    }


    @Test
    @Order(5)
    public void deleteWeekReport() throws Exception {
        authUser(TEST_TRACKER_EMAIL);

        Task task = createTask(tracking, startup, tracker);
        TaskComment taskComment = createTaskComment(task, tracker);

        mockMvc.perform(
                delete(API_PREFIX + taskComment.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk());
    }


    @AfterAll
    public void teardown() {
        startupRepository.deleteAll();
        startupTrackingRepository.deleteAll();
        trackingRepository.deleteAll();
        userRepository.findByEmail(TEST_STARTUP_EMAIL).ifPresent(userRepository::delete);
        userRepository.findByEmail(TEST_TRACKER_EMAIL).ifPresent(userRepository::delete);
    }

    private void setStartupOwner(Startup startup) {
        if (userRepository.findByEmail(TEST_STARTUP_EMAIL).isPresent()) {
            startup.setOwner(userRepository.findByEmail(TEST_STARTUP_EMAIL).get());
        }
    }


    private Startup createTestStartupObject() {
        Startup startup = new Startup();
        startup.setName(faker.name().name());
        setStartupOwner(startup);
        startupRepository.save(startup);

        return startup;
    }

    private void createStartupOwner() {
        User owner = new User();
        owner.setName(faker.name().name());
        owner.setEmail(TEST_STARTUP_EMAIL);
        owner.setRole(RoleEnum.STARTUP.getRole());
        owner.setPassword(faker.phoneNumber().toString());
        userRepository.save(owner);
    }

    private User createTracker() {
        User tracker = new User();
        tracker.setName(faker.name().name());
        tracker.setEmail(TEST_TRACKER_EMAIL);
        tracker.setRole(RoleEnum.TRACKER.getRole());
        tracker.setPassword(faker.phoneNumber().toString());
        userRepository.save(tracker);

        return tracker;
    }

    private void authUser(String email) {
        jwt = jwtUtil.generateToken(myUserDetailsService.loadUserByUsername(email));
    }

    private Tracking createTrackingWithStartup(Startup startup) {
        Tracking tracking = new Tracking();
        tracking.setName(faker.name().firstName());
        tracking.setStartedAt(LocalDateTime.now());
        tracking.setEndedAt(LocalDateTime.now());
        trackingRepository.save(tracking);

        StartupTracking startupTracking = new StartupTracking();
        startupTracking.setStartup(startup);
        startupTracking.setTracking(tracking);
        if (userRepository.findByEmail(TEST_TRACKER_EMAIL).isPresent()) {
            startupTracking.setTracker(userRepository.findByEmail(TEST_TRACKER_EMAIL).get());
        }
        startupTrackingRepository.save(startupTracking);

        return tracking;
    }

    private Task createTask(Tracking tracking, Startup startup, User tracker) {
        Task task = new Task();
        task.setStartup(startup);
        task.setTracking(tracking);
        task.setTaskStatus(TaskStatusEnum.TODO.getTaskStatus());
        task.setName(faker.name().username());
        task.setOwner(tracker);

        taskRepository.save(task);

        return task;
    }

    private TaskComment createTaskComment(Task task, User user) {
        TaskComment taskComment = new TaskComment();
        taskComment.setOwner(user);
        taskComment.setTask(task);
        taskComment.setText(faker.demographic().educationalAttainment());

        taskCommentRepository.save(taskComment);

        return taskComment;
    }
}
