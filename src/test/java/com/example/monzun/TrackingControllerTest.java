package com.example.monzun;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.StartupTracking;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.repositories.StartupTrackingRepository;
import com.example.monzun.repositories.TrackingRepository;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.security.JwtRequestFilter;
import com.example.monzun.security.JwtUtil;
import com.example.monzun.services.MyUserDetailsService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrackingControllerTest extends BaseTest {
    private final String TEST_STARTUP_EMAIL = "TESTSTARTUP@ya.ru";
    private final String TEST_TRACKER_EMAIL = "TESTTRACKER@mail.ru";
    private Tracking tracking;
    private String jwt;
    private final String API_PREFIX = "/api/trackings/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private StartupTrackingRepository startupTrackingRepository;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        createStartupOwner();
        createTracker();
        Startup startup = createTestStartupObject();
        tracking = createTrackingWithStartup(startup);
    }


    @Test
    @Order(1)
    public void getTrackingsByStartupTest() throws Exception {
        authUser(TEST_STARTUP_EMAIL);

        mockMvc.perform(
                get(API_PREFIX)
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(tracking.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(tracking.getName())));
    }

    @Test
    @Order(2)
    public void getTrackingsByTrackerTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);

        mockMvc.perform(
                get(API_PREFIX)
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(tracking.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(tracking.getName())));
    }

    @Test
    @Order(3)
    public void getTrackingByStartupTest() throws Exception {
        authUser(TEST_STARTUP_EMAIL);
        mockMvc.perform(
                get(API_PREFIX + tracking.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tracking.getId().intValue())))
                .andExpect(jsonPath("$.name", is(tracking.getName())));
    }

    @Test
    @Order(4)
    public void getTrackingByTrackerTest() throws Exception {
        authUser(TEST_TRACKER_EMAIL);
        mockMvc.perform(
                get(API_PREFIX + tracking.getId())
                        .header(JwtRequestFilter.JWT_HEADER, JwtRequestFilter.JWT_HEADER_PREFIX + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tracking.getId().intValue())))
                .andExpect(jsonPath("$.name", is(tracking.getName())));
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

    private void createTracker() {
        User owner = new User();
        owner.setName(faker.name().name());
        owner.setEmail(TEST_TRACKER_EMAIL);
        owner.setRole(RoleEnum.TRACKER.getRole());
        owner.setPassword(faker.phoneNumber().toString());
        userRepository.save(owner);
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
}
