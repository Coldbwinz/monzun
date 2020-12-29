package com.example.monzun;

import com.example.monzun.entities.*;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.enums.TaskStatusEnum;
import com.example.monzun.repositories.*;
import com.example.monzun.security.JwtRequestFilter;
import com.example.monzun.security.JwtUtil;
import com.example.monzun.services.MyUserDetailsService;
import org.json.JSONObject;
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
public class TaskControllerTest extends BaseTest {
    private final String TEST_STARTUP_EMAIL = "TESTSTARTUP@ya.ru";
    private final String TEST_TRACKER_EMAIL = "TESTTRACKER@mail.ru";
    private Startup startup;
    private User tracker;
    private Tracking tracking;
    private String jwt;
    private final String API_PREFIX = "/api/tasks/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private TaskRepository taskRepository;

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
    public void getTasksByStartupTest() throws Exception {
        authUser(TEST_STARTUP_EMAIL);
        Task task = createTask(tracking, startup, tracker);

        mockMvc.perform(
                get(API_PREFIX + tracking.getId() + "/" + startup.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(task.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(task.getName())));
    }

    @Test
    @Order(2)
    public void getTasksByTrackerTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);
        Task task = createTask(tracking, startup, tracker);

        mockMvc.perform(
                get(API_PREFIX + tracking.getId() + "/" + startup.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(task.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(task.getName())));
    }

    @Test
    @Order(3)
    public void getTaskByStartupTest() throws Exception {
        authUser(TEST_STARTUP_EMAIL);
        Task task = createTask(tracking, startup, tracker);

        mockMvc.perform(
                get(API_PREFIX + task.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(task.getId().intValue())))
                .andExpect(jsonPath("$.name", is(task.getName())));
    }

    @Test
    @Order(4)
    public void getTaskByTrackerTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);
        Task task = createTask(tracking, startup, tracker);

        mockMvc.perform(
                get(API_PREFIX + task.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(task.getId().intValue())))
                .andExpect(jsonPath("$.name", is(task.getName())));
    }

    @Test
    @Order(5)
    public void createTaskTest() throws Exception {
        authUser(TEST_STARTUP_EMAIL);
        String name = faker.name().name();
        JSONObject params = new JSONObject();
        params.put("statusId", 1);
        params.put("name", name);

        mockMvc.perform(
                post(API_PREFIX + tracking.getId() + "/" + startup.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params.toString())
        )
                .andExpect(status().isForbidden());

        authUser(TEST_TRACKER_EMAIL);

        mockMvc.perform(
                post(API_PREFIX + tracking.getId() + "/" + startup.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params.toString())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)));
    }


    @Test
    @Order(6)
    public void updateTaskTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);
        Task task = createTask(tracking, startup, tracker);
        String name = faker.name().firstName();

        JSONObject params = new JSONObject();
        params.put("statusId", task.getTaskStatus().getTaskStatusId());
        params.put("name", name);

        mockMvc.perform(
                put(API_PREFIX + task.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params.toString())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(task.getId().intValue())))
                .andExpect(jsonPath("$.name", is(name)));
    }


    @Test
    @Order(7)
    public void deleteWeekReport() throws Exception {
        authUser(TEST_TRACKER_EMAIL);
        Task task = createTask(tracking, startup, tracker);

        mockMvc.perform(
                delete(API_PREFIX + task.getId())
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
}
